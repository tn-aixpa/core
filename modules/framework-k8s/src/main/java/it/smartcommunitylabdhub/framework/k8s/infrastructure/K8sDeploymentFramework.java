package it.smartcommunitylabdhub.framework.k8s.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.*;
import it.smartcommunitylabdhub.commons.annotations.infrastructure.FrameworkComponent;
import it.smartcommunitylabdhub.commons.jackson.JacksonMapper;
import it.smartcommunitylabdhub.commons.models.entities.log.Log;
import it.smartcommunitylabdhub.commons.models.entities.log.LogMetadata;
import it.smartcommunitylabdhub.commons.models.entities.run.RunState;
import it.smartcommunitylabdhub.commons.services.LogService;
import it.smartcommunitylabdhub.commons.services.RunService;
import it.smartcommunitylabdhub.framework.k8s.annotations.ConditionalOnKubernetes;
import it.smartcommunitylabdhub.framework.k8s.exceptions.K8sFrameworkException;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sDeploymentRunnable;
import it.smartcommunitylabdhub.fsm.StateMachine;
import it.smartcommunitylabdhub.fsm.enums.RunEvent;
import it.smartcommunitylabdhub.fsm.exceptions.StopPoller;
import it.smartcommunitylabdhub.fsm.pollers.PollingService;
import it.smartcommunitylabdhub.fsm.types.RunStateMachine;
import it.smartcommunitylabdhub.fsm.workflow.WorkflowFactory;
import java.util.*;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

//TODO: le operazioni di clean del deployment vanno fatte nel framework
@Slf4j
@FrameworkComponent(framework = "k8sdeployment")
public class K8sDeploymentFramework extends K8sBaseFramework<K8sDeploymentRunnable, V1Deployment> {

    private final AppsV1Api appsV1Api;

    //TODO drop
    @Autowired
    PollingService pollingService;

    //TODO drop from framework, this should be delegated to run listener/service
    //the framework has NO concept of runs, only RUNNABLEs
    @Autowired
    RunStateMachine runStateMachine;

    //TODO drop, logs must be handled by a listener
    @Autowired
    LogService logService;

    //TODO drop from framework, this should be delegated to run listener/service
    //the framework has NO concept of runs, only RUNNABLEs
    @Autowired
    RunService runService;

    public K8sDeploymentFramework(ApiClient apiClient) {
        super(apiClient);
        appsV1Api = new AppsV1Api(apiClient);
    }

    @Override
    public void execute(K8sDeploymentRunnable runnable) throws K8sFrameworkException {
        V1Deployment deployment = apply(runnable);

        //TODO refactor
        monitor(runnable, deployment);
    }

    // TODO: instead of void define a Result object that have to be merged with the run from the
    // caller.

    public V1Deployment apply(K8sDeploymentRunnable runnable) throws K8sFrameworkException {
        try {
            // Log service execution initiation
            log.info("----------------- PREPARE KUBERNETES Deployment ----------------");

            // Generate deploymentName and ContainerName
            String deploymentName = k8sBuilderHelper.getDeploymentName(
                runnable.getRuntime(),
                runnable.getTask(),
                runnable.getId()
            );
            String containerName = k8sBuilderHelper.getContainerName(
                runnable.getRuntime(),
                runnable.getTask(),
                runnable.getId()
            );

            // Create labels for job
            Map<String, String> labels = buildLabels(runnable);

            // Create the Deployment metadata
            V1ObjectMeta metadata = new V1ObjectMeta().name(deploymentName).labels(labels);

            // Prepare environment variables for the Kubernetes job
            List<V1EnvFromSource> envFrom = buildEnvFrom(runnable);
            List<V1EnvVar> env = buildEnv(runnable);

            // Volumes to attach to the pod based on the volume spec with the additional volume_type
            List<V1Volume> volumes = buildVolumes(runnable);
            List<V1VolumeMount> volumeMounts = buildVolumeMounts(runnable);

            // resources
            V1ResourceRequirements resources = buildResources(runnable);

            //command params
            List<String> command = buildCommand(runnable);
            List<String> args = buildArgs(runnable);

            // Build Container
            V1Container container = new V1Container()
                .name(containerName)
                .image(runnable.getImage())
                .imagePullPolicy("Always")
                .imagePullPolicy("IfNotPresent")
                .command(command)
                .args(args)
                .resources(resources)
                .volumeMounts(volumeMounts)
                .envFrom(envFrom)
                .env(env);

            // Create a PodSpec for the container
            V1PodSpec podSpec = new V1PodSpec()
                .containers(Collections.singletonList(container))
                .nodeSelector(buildNodeSelector(runnable))
                .affinity(runnable.getAffinity())
                .tolerations(buildTolerations(runnable))
                .volumes(volumes)
                .restartPolicy("Always");

            // Create a PodTemplateSpec with the PodSpec
            V1PodTemplateSpec podTemplateSpec = new V1PodTemplateSpec().metadata(metadata).spec(podSpec);

            // Create the JobSpec with the PodTemplateSpec
            V1DeploymentSpec deploymentSpec = new V1DeploymentSpec()
                // .completions(1)
                // .backoffLimit(6)    // is the default value
                .selector(new V1LabelSelector().matchLabels(labels))
                .template(podTemplateSpec);

            // Create the V1Deployment object with metadata and JobSpec
            V1Deployment deployment = new V1Deployment().metadata(metadata).spec(deploymentSpec);

            //dispatch deployment via api
            V1Deployment createdDeployment = appsV1Api.createNamespacedDeployment(
                namespace,
                deployment,
                null,
                null,
                null,
                null
            );
            log.info("Deployment created: {}", Objects.requireNonNull(createdDeployment.getMetadata()).getName());
            return createdDeployment;
        } catch (ApiException e) {
            log.error("Error with k8s: {}", e.getMessage());
            if (log.isDebugEnabled()) {
                log.debug("k8s api response: {}", e.getResponseBody());
            }

            throw new K8sFrameworkException(e.getMessage());
        }
    }

    private void monitor(K8sDeploymentRunnable runnable, V1Deployment deployment) {
        //TODO cleanup monitoring!
        if (pollingService == null || runStateMachine == null || logService == null || runService == null) {
            return;
        }

        // FIXME: DELETE THIS IS ONLY FOR DEBUG
        String threadName = Thread.currentThread().getName();

        // Generate deploymentName and ContainerName
        String deploymentName = k8sBuilderHelper.getDeploymentName(
            runnable.getRuntime(),
            runnable.getTask(),
            runnable.getId()
        );

        String containerName = k8sBuilderHelper.getContainerName(
            runnable.getRuntime(),
            runnable.getTask(),
            runnable.getId()
        );

        // Initialize the run state machine considering current state and context
        StateMachine<RunState, RunEvent, Map<String, Object>> fsm = runStateMachine.create(
            RunState.valueOf(runnable.getState()),
            Map.of("runId", runnable.getId())
        );

        // Log the initiation of Dbt Kubernetes Listener
        log.info("Dbt Kubernetes Listener [" + threadName + "] " + deploymentName + "@" + namespace);

        // Define a function with parameters
        Function<
            String,
            Function<String, Function<StateMachine<RunState, RunEvent, Map<String, Object>>, Void>>
        > checkDeploymentStatus = dName ->
            cName ->
                fMachine -> {
                    try {
                        V1Deployment v1Deployment = appsV1Api.readNamespacedDeployment(dName, namespace, null);
                        V1DeploymentStatus v1DeploymentStatus = v1Deployment.getStatus();

                        assert v1DeploymentStatus != null;
                        Objects
                            .requireNonNull(v1DeploymentStatus.getConditions())
                            .forEach(v -> log.info(v.getStatus()));
                        //                // Check the Deployment status
                        //                if (Objects.requireNonNull(v1DeploymentStatus).getReadyReplicas() != null
                        //                        && !fMachine.getCurrentState().equals(RunState.COMPLETED)) {
                        //
                        //
                        //                    // Deployment has completed successfully
                        //                    log.info("Deployment completed successfully.");
                        //                    // Update state machine and update runDTO
                        //                    fMachine.goToState(RunState.COMPLETED);
                        //                    Run runDTO = runService.getRun(runnable.getId());
                        //                    runDTO.getStatus().put("state", fsm.getCurrentState().name());
                        //                    runService.updateRun(runDTO, runDTO.getId());
                        //
                        //                    // Log pod status
                        //                    logPod(dName, cName, namespace, runnable);
                        //                    // Delete job and pod
                        //                    //deleteAssociatedPodAndJob(dName, namespace, runnable);
                        //
                        //                } else if (Objects.requireNonNull(v1DeploymentStatus).getFailed() != null) {
                        //                    // Deployment has failed delete job and pod
                        //                    //deleteAssociatedPodAndJob(dName, namespace, runnable);
                        //
                        //                } else if (v1DeploymentStatus.getActive() != null && v1DeploymentStatus.getActive() > 0) {
                        //                    if (!fMachine.getCurrentState().equals(RunState.RUNNING)) {
                        //                        fMachine.goToState(RunState.READY);
                        //                        fMachine.goToState(RunState.RUNNING);
                        //                    }
                        //                    log.warn("Deployment is running...");
                        //                    logPod(dName, cName, namespace, runnable);
                        //                } else {
                        //                    String v1JobStatusString = JacksonMapper.CUSTOM_OBJECT_MAPPER.writeValueAsString(v1DeploymentStatus);
                        //                    log.warn("Deployment is in an unknown state : " + v1JobStatusString);
                        //                    writeLog(runnable, v1JobStatusString);
                        //                }

                    } catch (ApiException e) {
                        log.error("Error with k8s: {}", e.getMessage());
                        if (log.isDebugEnabled()) {
                            log.debug("k8s api response: {}", e.getResponseBody());
                        }

                        deleteAssociatedPodAndJob(dName, namespace, runnable);
                        throw new StopPoller(e.getMessage());
                    }

                    return null;
                };

        // Using the step method with explicit arguments
        pollingService.createPoller(
            runnable.getId(),
            List.of(
                WorkflowFactory
                    .builder()
                    .step(
                        (Function<?, ?>) i ->
                            checkDeploymentStatus.apply(deploymentName).apply(containerName).apply(fsm)
                    )
                    .build()
            ),
            1,
            true,
            false
        );

        // Start job poller
        pollingService.startOne(runnable.getId());
    }

    //TODO drop
    private void writeLog(K8sDeploymentRunnable runnable, String log) {
        if (logService != null) {
            LogMetadata logMetadata = new LogMetadata();
            logMetadata.setProject(runnable.getProject());
            logMetadata.setRun(runnable.getId());
            Log logDTO = Log.builder().body(Map.of("content", log)).metadata(logMetadata.toMap()).build();
            logService.createLog(logDTO);
        }
    }

    // /**
    //  * Logging pod
    //  *
    //  * @param jobName  the name of the Deployment
    //  * @param runnable the runnable Type in this case K8SJobRunnable
    //  */
    // private void logPod(String jobName, String cName, String namespace, K8sDeploymentRunnable runnable) {
    //     try {
    //         // Retrieve and print the logs of the associated Pod
    //         V1PodList v1PodList = coreV1Api.listNamespacedPod(
    //             namespace,
    //             null,
    //             null,
    //             null,
    //             null,
    //             null,
    //             null,
    //             null,
    //             null,
    //             null,
    //             null,
    //             null
    //         );

    //         for (V1Pod pod : v1PodList.getItems()) {
    //             if (pod.getMetadata() != null && pod.getMetadata().getName() != null) {
    //                 if (pod.getMetadata().getName().startsWith(jobName)) {
    //                     String podName = pod.getMetadata().getName();
    //                     String logs = coreV1Api.readNamespacedPodLog(
    //                         podName,
    //                         namespace,
    //                         cName,
    //                         false,
    //                         null,
    //                         null,
    //                         null,
    //                         null,
    //                         null,
    //                         null,
    //                         null
    //                     );

    //                     log.info("Logs for Pod: " + podName);
    //                     log.info("Log is: " + logs);
    //                     if (logs != null) writeLog(runnable, logs);
    //                 }
    //             }
    //         }
    //     } catch (ApiException e) {
    //         log.error(e.getResponseBody());
    //         //throw new RuntimeException(e);
    //     }
    // }

    /**
     * Delete job
     *
     * @param jobName  the name of the Deployment
     * @param runnable the runnable Type in this case K8SJobRunnable
     */
    private void deleteAssociatedPodAndJob(String jobName, String namespace, K8sDeploymentRunnable runnable) {
        // Delete the Pod associated with the Deployment
        try {
            V1PodList v1PodList = coreV1Api.listNamespacedPod(
                namespace,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
            );

            for (V1Pod pod : v1PodList.getItems()) {
                if (pod.getMetadata() != null && pod.getMetadata().getName() != null) {
                    if (pod.getMetadata().getName().startsWith(jobName)) {
                        String podName = pod.getMetadata().getName();

                        // Delete the Pod
                        V1Pod v1Pod = coreV1Api.deleteNamespacedPod(
                            podName,
                            namespace,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null
                        );
                        log.info("Pod deleted: " + podName);

                        try {
                            writeLog(
                                runnable,
                                JacksonMapper.CUSTOM_OBJECT_MAPPER.writeValueAsString(v1Pod.getStatus())
                            );
                        } catch (JsonProcessingException e) {
                            log.error(e.toString());
                        }

                        // // Delete the Deployment
                        // V1Status deleteStatus = batchV1Api.deleteNamespacedJob(
                        //         jobName, "default", null,
                        //         null, null, null,
                        //         null, null);

                        // try {
                        //     writeLog(runnable, JacksonMapper.CUSTOM_OBJECT_MAPPER.writeValueAsString(deleteStatus));
                        // } catch (JsonProcessingException e) {
                        //     log.error(e.toString());
                        // }
                        log.info("Deployment deleted: " + jobName);
                    }
                }
            }
            throw new StopPoller("POLLER STOP SUCCESSFULLY");
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }
}
