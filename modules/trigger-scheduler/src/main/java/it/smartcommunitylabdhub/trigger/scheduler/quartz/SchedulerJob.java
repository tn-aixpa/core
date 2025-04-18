package it.smartcommunitylabdhub.trigger.scheduler.quartz;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.quartz.QuartzJobBean;

import it.smartcommunitylabdhub.trigger.scheduler.events.TriggerExecutionEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SchedulerJob extends QuartzJobBean  {
	
	ApplicationEventPublisher applicationEventPublisher;
	
	@Autowired
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}
	
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		String triggerId = (String) context.getJobDetail().getJobDataMap().get("triggerId");
        log.debug("triggered {}", triggerId);        
        TriggerExecutionEvent event = TriggerExecutionEvent.builder().id(triggerId).event("FIRE").build();
        applicationEventPublisher.publishEvent(event);			
	}
}
