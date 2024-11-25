package it.smartcommunitylabdhub.core.components.run;

import it.smartcommunitylabdhub.commons.models.run.Run;
import it.smartcommunitylabdhub.core.models.events.EntityAction;
import it.smartcommunitylabdhub.core.models.events.EntityOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class RunOperationsListener {

    private RunLifecycleManager runManager;

    @Autowired
    public void setRunManager(RunLifecycleManager runManager) {
        this.runManager = runManager;
    }

    @Async
    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void receive(EntityOperation<Run> event) {
        log.debug("receive operation for {}", event.getAction());

        Run dto = event.getDto();
        if (log.isTraceEnabled()) {
            log.trace("run: {}", String.valueOf(dto));
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
