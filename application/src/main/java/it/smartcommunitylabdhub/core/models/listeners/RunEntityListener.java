package it.smartcommunitylabdhub.core.models.listeners;

import it.smartcommunitylabdhub.commons.models.run.Run;
import it.smartcommunitylabdhub.core.models.entities.RunEntity;
import it.smartcommunitylabdhub.core.models.events.EntityEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.convert.converter.Converter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class RunEntityListener extends AbstractEntityListener<RunEntity, Run> {

    public RunEntityListener(Converter<RunEntity, Run> converter) {
        super(converter);
    }

    @Async
    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void receive(EntityEvent<RunEntity> event) {
        log.debug("receive event for {} {}", clazz.getSimpleName(), event.getAction());

        RunEntity entity = event.getEntity();
        RunEntity prev = event.getPrev();
        if (log.isTraceEnabled()) {
            log.trace("{}: {}", clazz.getSimpleName(), String.valueOf(entity));
        }

        //handle event if either: prev == null (for create/delete), prev != null and state has changed (update)
        if (prev == null || (prev != null && prev.getState() != entity.getState())) {
            //handle
            super.handle(event);
        }
        //TODO update project?
    }
}
