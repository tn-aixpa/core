package it.smartcommunitylabdhub.framework.k8s.infrastructure.monitor;

import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1Service;
import it.smartcommunitylabdhub.commons.annotations.infrastructure.MonitorComponent;
import it.smartcommunitylabdhub.commons.events.RunnableChangedEvent;
import it.smartcommunitylabdhub.commons.events.RunnableMonitorObject;
import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.services.RunnableStore;
import it.smartcommunitylabdhub.framework.k8s.annotations.ConditionalOnKubernetes;
import it.smartcommunitylabdhub.framework.k8s.exceptions.K8sFrameworkException;
import it.smartcommunitylabdhub.framework.k8s.infrastructure.k8s.K8sDeploymentFramework;
import it.smartcommunitylabdhub.framework.k8s.infrastructure.k8s.K8sServeFramework;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sServeRunnable;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.Assert;

@Slf4j
@ConditionalOnKubernetes
@MonitorComponent(framework = K8sServeFramework.FRAMEWORK)
public class K8sServeMonitor implements K8sBaseMonitor<Void> {

    private final K8sServeFramework k8sServeFramework;
    private final RunnableStore<K8sServeRunnable> runnableStore;
    private final ApplicationEventPublisher eventPublisher;
    private final K8sDeploymentFramework deploymentFramework;

    public K8sServeMonitor(
        K8sServeFramework k8sServeFramework,
        RunnableStore<K8sServeRunnable> runnableStore,
        ApplicationEventPublisher eventPublisher,
        K8sDeploymentFramework deploymentFramework
    ) {
        this.k8sServeFramework = k8sServeFramework;
        this.runnableStore = runnableStore;
        this.eventPublisher = eventPublisher;
        this.deploymentFramework = deploymentFramework;
    }

    @Override
    public Void monitor() {
        runnableStore
            .findAll()
            .stream()
            .filter(runnable -> runnable.getState() != null && runnable.getState().equals("RUNNING"))
            .flatMap(runnable -> {
                try {
                    V1Deployment v1Deployment = deploymentFramework.get(k8sServeFramework.buildDeployment(runnable));
                    V1Service v1Service = k8sServeFramework.get(k8sServeFramework.build(runnable));

                    // check status
                    Assert.notNull(
                        Objects.requireNonNull(v1Deployment.getStatus()).getReadyReplicas(),
                        "Deployment not ready"
                    );
                    Assert.isTrue(v1Deployment.getStatus().getReadyReplicas() > 0, "Deployment not ready");
                    Assert.notNull(v1Service.getStatus(), "Service not ready");

                    System.out.println("deployment status: " + v1Deployment.getStatus().getReadyReplicas());
                    System.out.println("service status: " + v1Service.getStatus());
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

        //        //TODO cleanup monitoring!
        //        if (pollingService == null || runStateMachine == null || logService == null || runService == null) {
        //            return;
        //        }
        //
        //        // FIXME: DELETE THIS IS ONLY FOR DEBUG
        //        String threadName = Thread.currentThread().getName();
        //
        //        // Generate deploymentName and ContainerName
        //        String deploymentName = k8sBuilderHelper.getDeploymentName(
        //                runnable.getRuntime(),
        //                runnable.getTask(),
        //                runnable.getId()
        //        );
        //
        //        String containerName = k8sBuilderHelper.getContainerName(
        //                runnable.getRuntime(),
        //                runnable.getTask(),
        //                runnable.getId()
        //        );
        //
        //        // Initialize the run state machine considering current state and context
        //        StateMachine<RunState, RunEvent, Map<String, Object>> fsm = runStateMachine.create(
        //                RunState.valueOf(runnable.getState()),
        //                Map.of("runId", runnable.getId())
        //        );
        //
        //        // Log the initiation of Dbt Kubernetes Listener
        //        log.info("Dbt Kubernetes Listener [" + threadName + "] " + deploymentName + "@" + namespace);
        //
        //        // Define a function with parameters
        //        Function<
        //                String,
        //                Function<String, Function<StateMachine<RunState, RunEvent, Map<String, Object>>, Void>>
        //                > checkDeploymentStatus = dName ->
        //                cName ->
        //                        fMachine -> {
        //                            try {
        //                                V1Deployment v1Deployment = appsV1Api.readNamespacedDeployment(dName, namespace, null);
        //                                V1DeploymentStatus v1DeploymentStatus = v1Deployment.getStatus();
        //
        //                                assert v1DeploymentStatus != null;
        //                                Objects
        //                                        .requireNonNull(v1DeploymentStatus.getConditions())
        //                                        .forEach(v -> log.info(v.getStatus()));
        //                                //                // Check the Deployment status
        //                                //                if (Objects.requireNonNull(v1DeploymentStatus).getReadyReplicas() != null
        //                                //                        && !fMachine.getCurrentState().equals(RunState.COMPLETED)) {
        //                                //
        //                                //
        //                                //                    // Deployment has completed successfully
        //                                //                    log.info("Deployment completed successfully.");
        //                                //                    // Update state machine and update runDTO
        //                                //                    fMachine.goToState(RunState.COMPLETED);
        //                                //                    Run runDTO = runService.getRun(runnable.getId());
        //                                //                    runDTO.getStatus().put("state", fsm.getCurrentState().name());
        //                                //                    runService.updateRun(runDTO, runDTO.getId());
        //                                //
        //                                //                    // Log pod status
        //                                //                    logPod(dName, cName, namespace, runnable);
        //                                //                    // Delete job and pod
        //                                //                    //deleteAssociatedPodAndJob(dName, namespace, runnable);
        //                                //
        //                                //                } else if (Objects.requireNonNull(v1DeploymentStatus).getFailed() != null) {
        //                                //                    // Deployment has failed delete job and pod
        //                                //                    //deleteAssociatedPodAndJob(dName, namespace, runnable);
        //                                //
        //                                //                } else if (v1DeploymentStatus.getActive() != null && v1DeploymentStatus.getActive() > 0) {
        //                                //                    if (!fMachine.getCurrentState().equals(RunState.RUNNING)) {
        //                                //                        fMachine.goToState(RunState.READY);
        //                                //                        fMachine.goToState(RunState.RUNNING);
        //                                //                    }
        //                                //                    log.warn("Deployment is running...");
        //                                //                    logPod(dName, cName, namespace, runnable);
        //                                //                } else {
        //                                //                    String v1JobStatusString = JacksonMapper.CUSTOM_OBJECT_MAPPER.writeValueAsString(v1DeploymentStatus);
        //                                //                    log.warn("Deployment is in an unknown state : " + v1JobStatusString);
        //                                //                    writeLog(runnable, v1JobStatusString);
        //                                //                }
        //
        //                            } catch (ApiException e) {
        //                                log.error("Error with k8s: {}", e.getMessage());
        //                                if (log.isDebugEnabled()) {
        //                                    log.debug("k8s api response: {}", e.getResponseBody());
        //                                }
        //
        //                                deleteAssociatedPodAndJob(dName, namespace, runnable);
        //                                throw new StopPoller(e.getMessage());
        //                            }
        //
        //                            return null;
        //                        };
        return null;
    }
}
