package it.smartcommunitylabdhub.trigger.scheduler.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationEventPublisher;

import it.smartcommunitylabdhub.trigger.scheduler.events.TriggerExecutionEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SchedulerJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
		String triggerId = (String) context.getJobDetail().getJobDataMap().get("triggerId");
        log.debug("triggered {}", triggerId);
        ApplicationEventPublisher applicationEventPublisher;
		try {
			applicationEventPublisher = (ApplicationEventPublisher) context.getScheduler().getContext().get("eventPublisher");
	        TriggerExecutionEvent event = TriggerExecutionEvent.builder().id(triggerId).event("FIRE").build();
	        applicationEventPublisher.publishEvent(event);			
		} catch (SchedulerException e) {
			log.warn("job execution error: {}, {}", triggerId, e.getMessage());
		}
    }
}
