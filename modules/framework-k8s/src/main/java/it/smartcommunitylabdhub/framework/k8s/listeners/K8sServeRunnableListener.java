package it.smartcommunitylabdhub.framework.k8s.listeners;

import it.smartcommunitylabdhub.commons.events.RunChangedEvent;
import it.smartcommunitylabdhub.commons.events.RunMonitorObject;
import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.services.RunnableStore;
import it.smartcommunitylabdhub.framework.k8s.annotations.ConditionalOnKubernetes;
import it.smartcommunitylabdhub.framework.k8s.exceptions.K8sFrameworkException;
import it.smartcommunitylabdhub.framework.k8s.infrastructure.k8s.K8sServeFramework;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sServeRunnable;
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
public class K8sServeRunnableListener {

    @Autowired
    K8sServeFramework k8sServeFramework;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    private RunnableStore<K8sServeRunnable> runnableStore;

    @Async
    @EventListener
    public void listen(K8sServeRunnable runnable) {
        Assert.notNull(runnable, "runnable can not be null");
        Assert.hasText(runnable.getId(), "runnable id can not be null or empty");
        log.info("Receive runnable for execution: {}", runnable.getId());

        try {
            runnable =
                    switch (State.valueOf(runnable.getState())) {
                        case State.READY: {
                            yield k8sServeFramework.run(runnable);
                        }
                        case State.STOP: {
                            yield k8sServeFramework.stop(runnable);
                        }
                        case State.DELETED: {
                            yield k8sServeFramework.delete(runnable);
                        }
                        default: {
                            yield null;
                        }
                    };

            if (runnable != null) {
                try {
                    runnableStore.store(runnable.getId(), runnable);
                    // Publish event to Run Manager
                    eventPublisher.publishEvent(
                            RunChangedEvent
                                    .builder()
                                    .runnable(runnable)
                                    .runMonitorObject(
                                            RunMonitorObject
                                                    .builder()
                                                    .runId(runnable.getId())
                                                    .stateId(runnable.getState())
                                                    .project(runnable.getProject())
                                                    .framework(runnable.getFramework())
                                                    .task(runnable.getTask())
                                                    .build()
                                    )
                                    .build());
                } catch (StoreException e) {
                    log.error("Error with store: {}", e.getMessage());
                }

            }
        } catch (K8sFrameworkException e) {
            // Set runnable to error state send event
            log.error("Error with k8s: {}", e.getMessage());
            runnable.setState(State.ERROR.name());

            try {
                runnableStore.store(runnable.getId(), runnable);
                // Publish event to Run Manager
                eventPublisher.publishEvent(
                        RunChangedEvent
                                .builder()
                                .runnable(runnable)
                                .runMonitorObject(
                                        RunMonitorObject
                                                .builder()
                                                .runId(runnable.getId())
                                                .stateId(runnable.getState())
                                                .project(runnable.getProject())
                                                .framework(runnable.getFramework())
                                                .task(runnable.getTask())
                                                .build()
                                )
                                .build());
            } catch (StoreException sex) {
                log.error("Error with store: {}", sex.getMessage());
            }
        } finally {
            //remove after execution
            //TODO, needs to cleanup FSM usage in framework
            log.debug("Completed runnable {}", runnable.getId());
        }
    }
}
