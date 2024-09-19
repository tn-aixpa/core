package it.smartcommunitylabdhub.framework.kaniko.listeners;

import it.smartcommunitylabdhub.commons.events.RunnableChangedEvent;
import it.smartcommunitylabdhub.commons.events.RunnableMonitorObject;
import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.services.RunnableStore;
import it.smartcommunitylabdhub.framework.k8s.annotations.ConditionalOnKubernetes;
import it.smartcommunitylabdhub.framework.k8s.exceptions.K8sFrameworkException;
import it.smartcommunitylabdhub.framework.kaniko.infrastructure.k8s.K8sKanikoFramework;
import it.smartcommunitylabdhub.framework.kaniko.runnables.K8sKanikoRunnable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@ConditionalOnKubernetes
@Slf4j
public class K8sKanikoRunnableListener {

    @Autowired
    K8sKanikoFramework k8sFramework;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    private RunnableStore<K8sKanikoRunnable> runnableStore;

    @Async
    @EventListener
    public void listen(K8sKanikoRunnable runnable) {
        Assert.notNull(runnable, "runnable can not be null");
        Assert.hasText(runnable.getId(), "runnable id can not be null or empty");
        log.info("Receive runnable for execution: {}", runnable.getId());

        try {
            runnable =
                switch (State.valueOf(runnable.getState())) {
                    case State.READY -> {
                        yield k8sFramework.run(runnable);
                    }
                    case State.STOP -> {
                        yield k8sFramework.stop(runnable);
                    }
                    case State.DELETING -> {
                        yield k8sFramework.delete(runnable);
                    }
                    default -> {
                        yield null;
                    }
                };

            if (runnable != null) {
                try {
                    // If runnable is deleted, remove from store
                    if (runnable.getState().equals(State.DELETED.name())) {
                        runnableStore.remove(runnable.getId());
                    } else {
                        runnableStore.store(runnable.getId(), runnable);
                    }
                } catch (StoreException e) {
                    log.error("Error with store: {}", e.getMessage());
                }
            }
        } catch (K8sFrameworkException e) {
            // Set runnable to error state send event
            log.error("Error with k8s: {}", e.getMessage());
            runnable.setState(State.ERROR.name());
            runnable.setError(e.getClass().getSimpleName() + ":" + String.valueOf(e.getMessage()));

            try {
                runnableStore.store(runnable.getId(), runnable);
            } catch (StoreException ex) {
                log.error("Error with store: {}", ex.getMessage());
            }
        } finally {
            if (runnable != null) {
                log.debug("Processed runnable {}", runnable.getId());

                // Publish event to Run Manager
                eventPublisher.publishEvent(
                    RunnableChangedEvent
                        .builder()
                        .runnable(runnable)
                        .runMonitorObject(
                            RunnableMonitorObject
                                .builder()
                                .runId(runnable.getId())
                                .stateId(runnable.getState())
                                .project(runnable.getProject())
                                .framework(runnable.getFramework())
                                .task(runnable.getTask())
                                .build()
                        )
                        .build()
                );
            }
        }
    }
}
