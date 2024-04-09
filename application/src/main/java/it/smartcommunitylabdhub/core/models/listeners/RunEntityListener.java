package it.smartcommunitylabdhub.core.models.listeners;

import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.core.components.run.RunManager;
import it.smartcommunitylabdhub.core.models.entities.RunEntity;
import it.smartcommunitylabdhub.core.models.events.EntityAction;
import it.smartcommunitylabdhub.core.models.events.EntityEvent;
import it.smartcommunitylabdhub.core.models.events.EntityOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.convert.converter.Converter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class RunEntityListener extends AbstractEntityListener<RunEntity, Run> {

    private RunManager runManager;

    public RunEntityListener(Converter<RunEntity, Run> converter) {
        super(converter);
    }

    @Autowired
    public void setRunManager(RunManager runManager) {
        this.runManager = runManager;
    }

    @Async
    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void receive(EntityEvent<RunEntity> event) {
        log.debug("receive event for {} {}", clazz.getSimpleName(), event.getAction());

        RunEntity entity = event.getEntity();
        if (log.isTraceEnabled()) {
            log.trace("{}: {}", clazz.getSimpleName(), String.valueOf(entity));
        }
        //no-op for now
    }

    @Async
    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void receive(EntityOperation<Run> event) {
        log.debug("receive operation for {} {}", clazz.getSimpleName(), event.getAction());

        Run dto = event.getDto();
        if (log.isTraceEnabled()) {
            log.trace("{}: {}", clazz.getSimpleName(), String.valueOf(dto));
        }

        if (EntityAction.DELETE == event.getAction()) {
            //handle delete via manager
            if (runManager != null) {
                //delete via manager
                runManager.delete(dto);
            }
        }
    }
}
