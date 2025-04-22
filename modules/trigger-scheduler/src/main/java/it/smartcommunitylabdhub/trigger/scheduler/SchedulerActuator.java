package it.smartcommunitylabdhub.trigger.scheduler;

import it.smartcommunitylabdhub.commons.annotations.infrastructure.ActuatorComponent;
import it.smartcommunitylabdhub.commons.infrastructure.Actuator;
import it.smartcommunitylabdhub.commons.infrastructure.TriggerRun;
import it.smartcommunitylabdhub.commons.models.trigger.Trigger;
import it.smartcommunitylabdhub.commons.models.trigger.TriggerBaseStatus;
import it.smartcommunitylabdhub.commons.models.trigger.TriggerRunBaseStatus;
import it.smartcommunitylabdhub.trigger.scheduler.models.SchedulerTriggerSpec;
import it.smartcommunitylabdhub.trigger.scheduler.quartz.SchedulerJob;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@ActuatorComponent(actuator = SchedulerActuator.ACTUATOR)
public class SchedulerActuator implements Actuator<SchedulerTriggerSpec, TriggerBaseStatus, TriggerRunBaseStatus> {

    @Autowired
    Scheduler scheduler;

    public static final String ACTUATOR = "scheduler";

    @Override
    public TriggerBaseStatus run(@NotNull Trigger trigger) {
        log.debug("create job for {}", trigger.getKey());
        JobKey jobKey = JobKey.jobKey(trigger.getKey(), ACTUATOR);
        SchedulerTriggerSpec spec = SchedulerTriggerSpec.from(trigger.getSpec());
        JobDetail jobDetail = JobBuilder.newJob().ofType(SchedulerJob.class).withIdentity(jobKey).build();
        jobDetail.getJobDataMap().put("triggerId", trigger.getId());
        CronTrigger cronTrigger = TriggerBuilder
            .newTrigger()
            .forJob(jobDetail)
            .withIdentity(trigger.getKey(), ACTUATOR)
            .withSchedule(CronScheduleBuilder.cronSchedule(spec.getSchedule()))
            .startNow()
            .build();
        try {
            scheduler.scheduleJob(jobDetail, cronTrigger);
        } catch (SchedulerException e) {
            log.warn("run error: {}, {}", trigger.getKey(), e.getMessage());
        }
        TriggerBaseStatus baseStatus = TriggerBaseStatus.with(trigger.getStatus());
        return baseStatus;
    }

    @Override
    public TriggerBaseStatus stop(@NotNull Trigger trigger) {
        log.debug("delete job for {}", trigger.getKey());
        JobKey jobKey = JobKey.jobKey(trigger.getKey(), ACTUATOR);
        try {
            boolean deleteJob = scheduler.deleteJob(jobKey);
            log.debug("job deleted for {}, {}", trigger.getKey(), deleteJob);
        } catch (SchedulerException e) {
            log.warn("run error: {}, {}", trigger.getKey(), e.getMessage());
        }
        TriggerBaseStatus baseStatus = TriggerBaseStatus.with(trigger.getStatus());
        return baseStatus;
    }

    @Override
    public TriggerRunBaseStatus onFire(@NotNull Trigger trigger, TriggerRun run) {
        //throw new UnsupportedOperationException("Unimplemented method 'onFire'");
        return null;
    }
}
