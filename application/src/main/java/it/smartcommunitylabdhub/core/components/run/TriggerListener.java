package it.smartcommunitylabdhub.core.components.run;

import it.smartcommunitylabdhub.commons.models.trigger.Trigger;
import it.smartcommunitylabdhub.commons.services.TriggerService;
import it.smartcommunitylabdhub.core.fsm.TriggerEvent;
import it.smartcommunitylabdhub.trigger.scheduler.events.TriggerExecutionEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TriggerListener {

    @Autowired
    TriggerService triggerService;

    @Autowired
    TriggerLifecycleManager triggerManager;

    @Async
    @EventListener
    public void receive(TriggerExecutionEvent event) {
        if (StringUtils.isEmpty(event.getEvent()) || StringUtils.isEmpty(event.getId())) {
            return;
        }
        log.debug("receive event {}, {}", event.getEvent(), event.getId());

        Trigger trigger = triggerService.findTrigger(event.getId());
        if (trigger == null) {
            return;
        }
        TriggerEvent te = TriggerEvent.valueOf(event.getEvent());

        switch (te) {
            case FIRE:
                triggerManager.fire(trigger);
                break;
            case RUN:
                triggerManager.run(trigger);
                break;
            case STOP:
                break;
            case ERROR:
                break;
        }
    }
}
