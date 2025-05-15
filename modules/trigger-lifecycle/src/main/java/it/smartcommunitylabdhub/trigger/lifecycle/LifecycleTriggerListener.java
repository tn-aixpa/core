/**
 * Copyright 2025 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.smartcommunitylabdhub.trigger.lifecycle;

import it.smartcommunitylabdhub.commons.accessors.fields.StatusFieldAccessor;
import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import it.smartcommunitylabdhub.commons.infrastructure.TriggerRun;
import it.smartcommunitylabdhub.commons.lifecycle.LifecycleEvent;
import it.smartcommunitylabdhub.commons.lifecycle.LifecycleEvents;
import it.smartcommunitylabdhub.commons.models.base.BaseDTO;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.models.status.StatusDTO;
import it.smartcommunitylabdhub.commons.models.trigger.TriggerEvent;
import it.smartcommunitylabdhub.commons.models.trigger.TriggerExecutionEvent;
import it.smartcommunitylabdhub.trigger.lifecycle.models.LifecycleTriggerJob;
import it.smartcommunitylabdhub.trigger.lifecycle.store.TriggerJobStore;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

@Slf4j
public class LifecycleTriggerListener {

    private TriggerJobStore<LifecycleTriggerJob> store;
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Autowired(required = false)
    public void setStore(TriggerJobStore<LifecycleTriggerJob> store) {
        this.store = store;
    }

    @Async
    @EventListener
    public <D extends BaseDTO & StatusDTO> void receive(LifecycleEvent<D, State, LifecycleEvents> event) {
        if (event.getEvent() == null) {
            return;
        }

        if (store == null) {
            log.warn("store is not set");
            return;
        }

        if (applicationEventPublisher == null) {
            log.error("applicationEventPublisher is null");
            return;
        }

        try {
            log.debug("receive event {} for {}", event.getEvent(), event.getId());
            if (log.isTraceEnabled()) {
                log.trace("event: {}", event);
            }

            String id = event.getId();

            //load details
            D dto = event.getDto();
            if (dto == null) {
                log.error("Missing dto");
                return;
            }

            String state = event.getState() != null ? event.getState().name() : null;
            if (state == null) {
                //read from dto status
                StatusFieldAccessor accessor = StatusFieldAccessor.with(dto.getStatus());
                state = accessor.getState();
            }

            if (state == null) {
                log.error("Missing or invalid state");
                return;
            }

            //pick matching jobs
            List<LifecycleTriggerJob> jobs = store.findMatchingKeyAndState(dto.getKey(), state);

            //submit fire for all these jobs
            jobs.forEach(job -> {
                //build a run for this job
                Map<String, Serializable> details = Map.of(
                    "timestamp",
                    Date.from(Instant.now()).getTime(),
                    "key",
                    job.getKey(),
                    "state",
                    job.getState(),
                    "input",
                    dto
                );

                TriggerRun<LifecycleTriggerJob> run = TriggerRun
                    .<LifecycleTriggerJob>builder()
                    .job(job)
                    .details(details)
                    .build();

                TriggerExecutionEvent<LifecycleTriggerJob> e = TriggerExecutionEvent
                    .<LifecycleTriggerJob>builder()
                    .run(run)
                    .event(TriggerEvent.FIRE)
                    .build();
                applicationEventPublisher.publishEvent(e);
            });
        } catch (StoreException e) {
            log.error("Error with store", e.getMessage());
        }
    }
}
