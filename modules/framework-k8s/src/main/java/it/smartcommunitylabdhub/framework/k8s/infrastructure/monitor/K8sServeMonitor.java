package it.smartcommunitylabdhub.framework.k8s.infrastructure.monitor;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import it.smartcommunitylabdhub.commons.annotations.infrastructure.MonitorComponent;

@MonitorComponent(framework = "serve")
public class K8sServeMonitor implements K8sBaseMonitor<Void> {

    protected CoreV1Api coreV1Api;
    private AppsV1Api appsV1Api;

    public K8sServeMonitor(ApiClient apiClient) {
        coreV1Api = new CoreV1Api(apiClient);
        appsV1Api = new AppsV1Api(apiClient);
    }

    @Override
    public Void monitor() {
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
