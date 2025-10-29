/*
 * SPDX-FileCopyrightText: © 2025 DSLab - Fondazione Bruno Kessler
 *
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * Copyright 2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package it.smartcommunitylabdhub.trigger.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.smartcommunitylabdhub.commons.exceptions.CoreRuntimeException;
import it.smartcommunitylabdhub.commons.jackson.JacksonMapper;
import it.smartcommunitylabdhub.commons.models.trigger.Trigger;
import it.smartcommunitylabdhub.trigger.scheduler.models.ScheduledTriggerJob;
import it.smartcommunitylabdhub.trigger.scheduler.models.SchedulerTriggerSpec;
import it.smartcommunitylabdhub.trigger.scheduler.quartz.SchedulerJob;
import it.smartcommunitylabdhub.triggers.infrastructure.Actuator;
import it.smartcommunitylabdhub.triggers.infrastructure.ActuatorComponent;
import it.smartcommunitylabdhub.triggers.infrastructure.TriggerRun;
import it.smartcommunitylabdhub.triggers.lifecycle.TriggerJob;
import it.smartcommunitylabdhub.triggers.models.TriggerRunBaseStatus;
import it.smartcommunitylabdhub.triggers.specs.TriggerBaseStatus;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

@Slf4j
@ActuatorComponent(actuator = SchedulerActuator.ACTUATOR)
public class SchedulerActuator implements Actuator<SchedulerTriggerSpec, TriggerBaseStatus, TriggerRunBaseStatus> {

    @Autowired
    Scheduler scheduler;

    public static final String ACTUATOR = "scheduler";

    @Override
    public TriggerBaseStatus run(@NotNull Trigger trigger) {
        try {
            log.debug("create job for {}", trigger.getKey());
            //define spec
            JobKey jobKey = JobKey.jobKey(trigger.getKey(), ACTUATOR);
            SchedulerTriggerSpec spec = SchedulerTriggerSpec.from(trigger.getSpec());

            //fetch and validate schedule
            //TODO proper exception handling
            String schedule = spec.getSchedule();
            if (!StringUtils.hasText(schedule)) {
                log.warn("invalid schedule for {}", trigger.getKey());
                throw new IllegalArgumentException("invalid schedule");
            }

            ScheduledTriggerJob job = ScheduledTriggerJob
                .builder()
                .id(trigger.getId())
                .user(trigger.getUser())
                .project(trigger.getProject())
                .task(spec.getTask())
                .project(trigger.getProject())
                .schedule(schedule)
                .build();

            //serialize job to avoid quartz classloader issues

            byte[] bytes = JacksonMapper.CBOR_OBJECT_MAPPER.writeValueAsBytes(job);

            //build quartz details
            JobDetail jobDetail = JobBuilder
                .newJob()
                .ofType(SchedulerJob.class)
                .withIdentity(jobKey)
                .usingJobData(new JobDataMap(Collections.singletonMap("job", bytes)))
                .build();

            org.quartz.Trigger qt = null;

            if (schedule.startsWith("@")) {
                //min delay at 1hour, we won't support jobs firing more frequently
                Date delay = Date.from(Instant.now().plus(1, ChronoUnit.HOURS));
                SimpleScheduleBuilder simpleSchedule = SimpleScheduleBuilder.simpleSchedule().repeatForever();
                String interval = schedule.substring(1);
                if ("hourly".equals(interval)) {
                    simpleSchedule = simpleSchedule.withIntervalInHours(1);
                } else if ("daily".equals(interval)) {
                    simpleSchedule = simpleSchedule.withIntervalInHours(24);
                } else if ("weekly".equals(interval)) {
                    simpleSchedule = simpleSchedule.withIntervalInHours(24 * 7);
                } else if ("monthly".equals(interval)) {
                    simpleSchedule = simpleSchedule.withIntervalInHours(24 * 30);
                } else {
                    log.warn("invalid schedule for {}", trigger.getKey());
                    throw new IllegalArgumentException("invalid schedule");
                }

                qt =
                    TriggerBuilder
                        .newTrigger()
                        .forJob(jobDetail)
                        .withIdentity(trigger.getKey(), ACTUATOR)
                        .withSchedule(simpleSchedule)
                        .startAt(delay)
                        .build();
            } else {
                //min delay at 1min, we won't support jobs firing more frequently
                Date delay = Date.from(Instant.now().plus(1, ChronoUnit.MINUTES));

                //TODO validate minimum interval
                //for now we check sub-seconds
                String[] split = schedule.split(" ");
                if (split.length < 6) {
                    log.warn("invalid schedule for {}", trigger.getKey());
                    throw new IllegalArgumentException("invalid schedule");
                }
                if ("*".equals(split[0])) {
                    //we don't support sub-seconds
                    log.warn("invalid schedule for {}", trigger.getKey());
                    throw new IllegalArgumentException("invalid schedule");
                }

                CronScheduleBuilder cronSchedule = CronScheduleBuilder.cronSchedule(schedule);

                qt =
                    TriggerBuilder
                        .newTrigger()
                        .forJob(jobDetail)
                        .withIdentity(trigger.getKey(), ACTUATOR)
                        .withSchedule(cronSchedule)
                        .startAt(delay)
                        .build();
            }

            if (qt == null) {
                throw new IllegalArgumentException("invalid trigger");
            }

            try {
                scheduler.scheduleJob(jobDetail, qt);
            } catch (SchedulerException e) {
                log.warn("run error: {}, {}", trigger.getKey(), e.getMessage());
            }

            TriggerBaseStatus baseStatus = TriggerBaseStatus.with(trigger.getStatus());
            return baseStatus;
        } catch (JsonProcessingException e) {
            throw new CoreRuntimeException("error serializing job");
        }
    }

    @Override
    public TriggerBaseStatus stop(@NotNull Trigger trigger) {
        log.debug("delete job for {}", trigger.getKey());

        //extract key
        JobKey jobKey = JobKey.jobKey(trigger.getKey(), ACTUATOR);
        try {
            boolean deleted = scheduler.deleteJob(jobKey);
            log.debug("job deleted for {}, {}", trigger.getKey(), deleted);
        } catch (SchedulerException e) {
            log.warn("run error: {}, {}", trigger.getKey(), e.getMessage());
        }

        TriggerBaseStatus baseStatus = TriggerBaseStatus.with(trigger.getStatus());
        return baseStatus;
    }

    @Override
    public TriggerRunBaseStatus onFire(@NotNull Trigger trigger, TriggerRun<? extends TriggerJob> run) {
        //nothing to do
        return null;
    }
}
