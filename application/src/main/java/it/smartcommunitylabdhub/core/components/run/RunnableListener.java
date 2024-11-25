package it.smartcommunitylabdhub.core.components.run;

import it.smartcommunitylabdhub.commons.infrastructure.RunRunnable;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.models.run.Run;
import it.smartcommunitylabdhub.commons.services.RunService;
import it.smartcommunitylabdhub.runtimes.events.RunnableChangedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Slf4j
public class RunnableListener {

    private final RunService runService;
    private final RunLifecycleManager runManager;

    public RunnableListener(RunService runService, RunLifecycleManager runManager) {
        Assert.notNull(runManager, "run manager is required");
        Assert.notNull(runService, "run service is required");
        this.runService = runService;
        this.runManager = runManager;
    }

    @Async
    @EventListener
    public void receive(RunnableChangedEvent<RunRunnable> event) {
        if (event.getState() == null) {
            return;
        }

        log.debug("onChanged run with id {}: {}", event.getId(), event.getState());
        if (log.isTraceEnabled()) {
            log.trace("event: {}", event);
        }

        // try {
        //read event
        String id = event.getId();
        State state = State.valueOf(event.getState());

        // Use service to retrieve the run and check if state is changed
        Run run = runService.findRun(id);
        if (run == null) {
            log.error("Run with id {} not found", id);
            return;
        }

        // if (
        //     //either signal an update or track progress (running state)
        //     !Objects.equals(StatusFieldAccessor.with(run.getStatus()).getState(), event.getState()) ||
        //     State.RUNNING == state
        // ) {
        switch (state) {
            case COMPLETED:
                runManager.onCompleted(run, event.getRunnable());
                break;
            case ERROR:
                runManager.onError(run, event.getRunnable());
                break;
            case RUNNING:
                runManager.onRunning(run, event.getRunnable());
                break;
            case STOPPED:
                runManager.onStopped(run, event.getRunnable());
                break;
            case DELETED:
                runManager.onDeleted(run, event.getRunnable());
                break;
            default:
                log.debug("State {} for run id {} not managed", state, id);
                break;
        }
        // } else {
        //     log.debug("State {} for run id {} not changed", state, id);
        // }
        // } catch (StoreException e) {
        //     log.error("store error for {}:{}", String.valueOf(event.getId()), e.getMessage());
        // }
    }
}
