package it.smartcommunitylabdhub.framework.k8s.listeners;

import it.smartcommunitylabdhub.commons.events.RunnableChangedEvent;
import it.smartcommunitylabdhub.commons.events.RunnableMonitorObject;
import it.smartcommunitylabdhub.commons.exceptions.FrameworkException;
import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import it.smartcommunitylabdhub.commons.infrastructure.RunRunnable;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.services.RunnableStore;
import it.smartcommunitylabdhub.framework.k8s.infrastructure.k8s.K8sBaseFramework;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sRunnable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.Assert;

@Slf4j
public class K8sRunnableListener<R extends K8sRunnable> {

    private final Class<R> clazz;

    private final K8sBaseFramework<R, ?> k8sFramework;

    private final RunnableStore<R> runnableStore;

    private ApplicationEventPublisher eventPublisher;

    @SuppressWarnings("unchecked")
    public K8sRunnableListener(K8sBaseFramework<R, ?> k8sFramework, RunnableStore<R> runnableStore) {
        Assert.notNull(k8sFramework, "k8sFramework can not be null");
        Assert.notNull(runnableStore, "runnableStore can not be null");

        this.k8sFramework = k8sFramework;
        this.runnableStore = runnableStore;

        this.clazz = (Class<R>) runnableStore.getResolvableType().resolve();
        log.debug("started listener for {}", clazz.getName());
    }

    @Async
    @EventListener
    public void listen(R runnable) {
        Assert.notNull(runnable, "runnable can not be null");
        Assert.hasText(runnable.getId(), "runnable id can not be null or empty");
        log.info(
            "Receive runnable {} for execution: {} state {}",
            clazz.getSimpleName(),
            runnable.getId(),
            runnable.getState()
        );

        if (log.isTraceEnabled()) {
            log.trace("runnable {}: {}", clazz.getSimpleName(), runnable);
        }

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
                if (log.isTraceEnabled()) {
                    log.trace("runnable result from framework {}: {}", clazz.getSimpleName(), runnable);
                }

                try {
                    // If runnable is deleted, remove from store
                    if (runnable.getState().equals(State.DELETED.name())) {
                        log.debug("remove runnable {} {} from store", clazz.getSimpleName(), runnable.getId());
                        runnableStore.remove(runnable.getId());
                    } else {
                        log.debug("update runnable {} {} in store", clazz.getSimpleName(), runnable.getId());
                        runnableStore.store(runnable.getId(), runnable);
                    }
                } catch (StoreException e) {
                    log.error("Error with store: {}", e.getMessage());
                }
            }
        } catch (FrameworkException e) {
            // Set runnable to error state send event
            log.error("Error with k8s for runnable {} {}: {}", clazz.getSimpleName(), runnable.getId(), e.getMessage());
            runnable.setState(State.ERROR.name());

            try {
                log.debug("update runnable {} {} in store", clazz.getSimpleName(), runnable.getId());
                runnableStore.store(runnable.getId(), runnable);
            } catch (StoreException se) {
                log.error("Error with store: {}", se.getMessage());
            }
        } finally {
            if (runnable != null) {
                log.debug("Processed runnable {} {}", clazz.getSimpleName(), runnable.getId());

                if (eventPublisher != null) {
                    log.debug("Publish runnable {} {}", clazz.getSimpleName(), runnable.getId());

                    RunnableChangedEvent<RunRunnable> event = RunnableChangedEvent
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
                        .build();

                    if (log.isTraceEnabled()) {
                        log.trace("runnable {} {} event {}", clazz.getSimpleName(), runnable.getId(), event);
                    }

                    // Publish event to Run Manager
                    eventPublisher.publishEvent(event);
                }
            }
        }
    }

    @Autowired
    public void setEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
}
