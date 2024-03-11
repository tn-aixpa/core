package it.smartcommunitylabdhub.framework.k8s.infrastructure.k8s;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.*;
import it.smartcommunitylabdhub.commons.annotations.infrastructure.FrameworkComponent;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.framework.k8s.exceptions.K8sFrameworkException;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sDeploymentRunnable;
import jakarta.validation.constraints.NotNull;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

//TODO: le operazioni di clean del deployment vanno fatte nel framework
@Slf4j
@FrameworkComponent(framework = K8sDeploymentFramework.FRAMEWORK)
public class K8sDeploymentFramework extends K8sBaseFramework<K8sDeploymentRunnable, V1Deployment> {

    public static final String FRAMEWORK = "k8sdeployment";

    private final AppsV1Api appsV1Api;


    public K8sDeploymentFramework(ApiClient apiClient) {
        super(apiClient);
        appsV1Api = new AppsV1Api(apiClient);
    }

    @Override
    public K8sDeploymentRunnable run(K8sDeploymentRunnable runnable) throws K8sFrameworkException {
        V1Deployment deployment = build(runnable);
        deployment = apply(deployment);

        runnable.setState(State.RUNNING.name());

        return runnable;
    }

    @Override
    public K8sDeploymentRunnable delete(K8sDeploymentRunnable runnable) throws K8sFrameworkException {

        V1Deployment deployment = get(build(runnable));
        Assert.notNull(deployment.getMetadata(), "metadata can not be null");

        try {
            appsV1Api.deleteNamespacedDeployment(
                    deployment.getMetadata().getName(),
                    namespace,
                    null, null,
                    null, null,
                    null, null);


            runnable.setState(State.DELETED.name());

        } catch (ApiException e) {
            log.error("Error with k8s: {}", e.getMessage());
            if (log.isDebugEnabled()) {
                log.debug("k8s api response: {}", e.getResponseBody());
            }

            throw new K8sFrameworkException(e.getMessage());
        }
        return runnable;


    }

    //TODO: instead of void define a Result object that have to be merged with the run from the
    // caller.

    public V1Deployment build(K8sDeploymentRunnable runnable) throws K8sFrameworkException {
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

        int replicas = Optional.ofNullable(runnable.getReplicas()).orElse(1);

        // Create the JobSpec with the PodTemplateSpec
        V1DeploymentSpec deploymentSpec = new V1DeploymentSpec()
                .replicas(replicas)
                .selector(new V1LabelSelector().matchLabels(labels))
                .template(podTemplateSpec);

        // Create the V1Deployment object with metadata and JobSpec
        return new V1Deployment().metadata(metadata).spec(deploymentSpec);
    }

    public V1Deployment apply(@NotNull V1Deployment deployment) throws K8sFrameworkException {
        Assert.notNull(deployment.getMetadata(), "metadata can not be null");

        try {
            // Log service execution initiation
            log.info("----------------- APPLY KUBERNETES Deployment ----------------");

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

    public V1Deployment get(@NotNull V1Deployment deployment) throws K8sFrameworkException {
        Assert.notNull(deployment.getMetadata(), "metadata can not be null");

        try {
            // Log service execution initiation
            log.info("----------------- GET KUBERNETES Deployment ----------------");

            return appsV1Api.readNamespacedDeployment(deployment.getMetadata().getName(), namespace, null);
        } catch (ApiException e) {
            log.error("Error with k8s: {}", e.getResponseBody());
            if (log.isDebugEnabled()) {
                log.debug("k8s api response: {}", e.getResponseBody());
            }

            throw new K8sFrameworkException(e.getResponseBody());
        }
    }


//    //TODO drop
//    private void writeLog(K8sDeploymentRunnable runnable, String log) {
//        if (logService != null) {
//            LogMetadata logMetadata = new LogMetadata();
//            logMetadata.setProject(runnable.getProject());
//            logMetadata.setRun(runnable.getId());
//            Log logDTO = Log.builder().body(Map.of("content", log)).metadata(logMetadata.toMap()).build();
//            logService.createLog(logDTO);
//        }
//    }


//    /**
//     * Delete job
//     *
//     * @param jobName  the name of the Deployment
//     * @param runnable the runnable Type in this case K8SJobRunnable
//     */
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
//    private void deleteAssociatedPodAndJob(String jobName, String namespace, K8sDeploymentRunnable runnable) {
//        // Delete the Pod associated with the Deployment
//        try {
//
//            V1PodList v1PodList = coreV1Api.listNamespacedPod(
//                    namespace,
//                    null,
//                    null,
//                    null,
//                    null,
//                    null,
//                    null,
//                    null,
//                    null,
//                    null,
//                    null,
//                    null
//            );
//
//            for (V1Pod pod : v1PodList.getItems()) {
//                if (pod.getMetadata() != null && pod.getMetadata().getName() != null) {
//                    if (pod.getMetadata().getName().startsWith(jobName)) {
//                        String podName = pod.getMetadata().getName();
//
//                        // Delete the Pod
//                        V1Pod v1Pod = coreV1Api.deleteNamespacedPod(
//                                podName,
//                                namespace,
//                                null,
//                                null,
//                                null,
//                                null,
//                                null,
//                                null
//                        );
//                        log.info("Pod deleted: " + podName);
//
//                        try {
//                            writeLog(
//                                    runnable,
//                                    JacksonMapper.CUSTOM_OBJECT_MAPPER.writeValueAsString(v1Pod.getStatus())
//                            );
//                        } catch (JsonProcessingException e) {
//                            log.error(e.toString());
//                        }
//
//                        // // Delete the Deployment
//                        // V1Status deleteStatus = batchV1Api.deleteNamespacedJob(
//                        //         jobName, "default", null,
//                        //         null, null, null,
//                        //         null, null);
//
//                        // try {
//                        //     writeLog(runnable, JacksonMapper.CUSTOM_OBJECT_MAPPER.writeValueAsString(deleteStatus));
//                        // } catch (JsonProcessingException e) {
//                        //     log.error(e.toString());
//                        // }
//                        log.info("Deployment deleted: " + jobName);
//                    }
//                }
//            }
//            throw new StopPoller("POLLER STOP SUCCESSFULLY");
//        } catch (ApiException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
