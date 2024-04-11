package it.smartcommunitylabdhub.framework.k8s.infrastructure.monitor;

import io.kubernetes.client.openapi.models.V1Deployment;
import it.smartcommunitylabdhub.commons.annotations.infrastructure.MonitorComponent;
import it.smartcommunitylabdhub.commons.events.RunnableChangedEvent;
import it.smartcommunitylabdhub.commons.events.RunnableMonitorObject;
import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.services.RunnableStore;
import it.smartcommunitylabdhub.framework.k8s.annotations.ConditionalOnKubernetes;
import it.smartcommunitylabdhub.framework.k8s.exceptions.K8sFrameworkException;
import it.smartcommunitylabdhub.framework.k8s.infrastructure.k8s.K8sDeploymentFramework;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sDeploymentRunnable;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.Assert;

@Slf4j
@ConditionalOnKubernetes
@MonitorComponent(framework = K8sDeploymentFramework.FRAMEWORK)
public class K8sDeploymentMonitor implements K8sBaseMonitor<Void> {

    private final K8sDeploymentFramework k8sDeploymentFramework;
    private final RunnableStore<K8sDeploymentRunnable> runnableStore;
    private final ApplicationEventPublisher eventPublisher;

    public K8sDeploymentMonitor(
        K8sDeploymentFramework k8sDeploymentFramework,
        RunnableStore<K8sDeploymentRunnable> runnableStore,
        ApplicationEventPublisher eventPublisher
    ) {
        this.k8sDeploymentFramework = k8sDeploymentFramework;
        this.runnableStore = runnableStore;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Void monitor() {
        runnableStore
            .findAll()
            .stream()
            .filter(runnable -> runnable.getState() != null && runnable.getState().equals("RUNNING"))
            .flatMap(runnable -> {
                try {
                    V1Deployment v1Deployment = k8sDeploymentFramework.get(k8sDeploymentFramework.build(runnable));

                    // check status
                    Assert.notNull(
                        Objects.requireNonNull(v1Deployment.getStatus()).getReadyReplicas(),
                        "Deployment not ready"
                    );
                    Assert.isTrue(v1Deployment.getStatus().getReadyReplicas() > 0, "Deployment not ready");

                    System.out.println("deployment status: " + v1Deployment.getStatus().getReadyReplicas());
                    return Stream.of(runnable);
                } catch (K8sFrameworkException e) {
                    // Set Runnable to ERROR state
                    runnable.setState(State.ERROR.name());
                    return Stream.of(runnable);
                }
            })
            .forEach(runnable -> {
                // Update the runnable
                try {
                    runnableStore.store(runnable.getId(), runnable);

                    // Send message to Serve manager
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
                } catch (StoreException e) {
                    log.error("Error with runnable store: {}", e.getMessage());
                }
            });
        return null;
    }
}
