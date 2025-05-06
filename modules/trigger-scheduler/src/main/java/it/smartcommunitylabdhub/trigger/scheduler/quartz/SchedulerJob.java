package it.smartcommunitylabdhub.trigger.scheduler.quartz;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.smartcommunitylabdhub.commons.infrastructure.TriggerRun;
import it.smartcommunitylabdhub.commons.jackson.JacksonMapper;
import it.smartcommunitylabdhub.commons.models.trigger.TriggerEvent;
import it.smartcommunitylabdhub.commons.models.trigger.TriggerExecutionEvent;
import it.smartcommunitylabdhub.commons.models.trigger.TriggerJob;
import it.smartcommunitylabdhub.trigger.scheduler.models.ScheduledTriggerJob;
import java.io.IOException;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.quartz.QuartzJobBean;

@Slf4j
public class SchedulerJob extends QuartzJobBean {

    ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    protected void executeInternal(@NonNull JobExecutionContext context) throws JobExecutionException {
        if (applicationEventPublisher == null) {
            log.error("applicationEventPublisher is null");
            return;
        }

        try {
            //fetch job data and publish FIRE event
            byte[] bytes = (byte[]) context.getJobDetail().getJobDataMap().get("job");
            ScheduledTriggerJob job = bytes != null
                ? JacksonMapper.CBOR_OBJECT_MAPPER.readValue(bytes, ScheduledTriggerJob.class)
                : null;

            log.debug("triggered {}", job.getId());
            if (log.isTraceEnabled()) {
                log.trace("job: {}", job);
            }

            //build a run for this job
            Map<String, Serializable> details = Map.of(
                "timestamp",
                Date.from(Instant.now()).getTime(),
                "schedule",
                job.getSchedule()
            );
            TriggerRun<TriggerJob> run = TriggerRun.<TriggerJob>builder().job(job).details(details).build();

            TriggerExecutionEvent<TriggerJob> event = TriggerExecutionEvent
                .builder()
                .run(run)
                .event(TriggerEvent.FIRE)
                .build();
            applicationEventPublisher.publishEvent(event);
        } catch (IOException e) {
            log.error("error deserializing job data: {}", e.getMessage());
        }
    }
}
