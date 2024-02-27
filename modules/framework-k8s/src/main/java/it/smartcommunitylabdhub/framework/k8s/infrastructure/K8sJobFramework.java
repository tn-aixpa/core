package it.smartcommunitylabdhub.framework.k8s.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.models.*;
import it.smartcommunitylabdhub.commons.annotations.infrastructure.FrameworkComponent;
import it.smartcommunitylabdhub.commons.jackson.JacksonMapper;
import it.smartcommunitylabdhub.commons.models.entities.log.Log;
import it.smartcommunitylabdhub.commons.models.entities.log.LogMetadata;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.entities.run.RunState;
import it.smartcommunitylabdhub.commons.services.LogService;
import it.smartcommunitylabdhub.commons.services.RunService;
import it.smartcommunitylabdhub.framework.k8s.exceptions.K8sFrameworkException;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sJobRunnable;
import it.smartcommunitylabdhub.fsm.StateMachine;
import it.smartcommunitylabdhub.fsm.enums.RunEvent;
import it.smartcommunitylabdhub.fsm.exceptions.StopPoller;
import it.smartcommunitylabdhub.fsm.pollers.PollingService;
import it.smartcommunitylabdhub.fsm.types.RunStateMachine;
import it.smartcommunitylabdhub.fsm.workflow.WorkflowFactory;
import jakarta.validation.constraints.NotNull;
import java.util.*;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

@Slf4j
@FrameworkComponent(framework = K8sJobFramework.FRAMEWORK)
public class K8sJobFramework extends K8sBaseFramework<K8sJobRunnable, V1Job> {

    public static final String FRAMEWORK = "k8sjob";

    public static final int DEADLINE_SECONDS = 3600 * 24 * 3; //3 days

    private final BatchV1Api batchV1Api;

    private int activeDeadlineSeconds = DEADLINE_SECONDS;

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

    //TODO drop from framework, this should be delegated to run listener
    //the framework has NO concept of runs, only RUNNABLEs
    @Autowired
    RunService runService;

    public K8sJobFramework(ApiClient apiClient) {
        super(apiClient);
        batchV1Api = new BatchV1Api(apiClient);
    }

    public void setActiveDeadlineSeconds(int activeDeadlineSeconds) {
        Assert.isTrue(activeDeadlineSeconds > 300, "Minimum deadline seconds is 300");
        this.activeDeadlineSeconds = activeDeadlineSeconds;
    }

    // TODO: instead of void define a Result object that have to be merged with the run from the
    // caller.
    @Override
    public void execute(K8sJobRunnable runnable) throws K8sFrameworkException {
        V1Job job = build(runnable);
        job = apply(job);

        //TODO refactor
        monitor(runnable, job);
    }

    public V1Job build(K8sJobRunnable runnable) throws K8sFrameworkException {
        // Log service execution initiation
        log.info("----------------- BUILD KUBERNETES JOB ----------------");

        // Generate jobName and ContainerName
        String jobName = k8sBuilderHelper.getJobName(runnable.getRuntime(), runnable.getTask(), runnable.getId());
        String containerName = k8sBuilderHelper.getContainerName(
            runnable.getRuntime(),
            runnable.getTask(),
            runnable.getId()
        );

        //build labels
        Map<String, String> labels = buildLabels(runnable);

        // Create the Job metadata
        V1ObjectMeta metadata = new V1ObjectMeta().name(jobName).labels(labels);

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
            .restartPolicy("Never");

        // Create a PodTemplateSpec with the PodSpec
        V1PodTemplateSpec podTemplateSpec = new V1PodTemplateSpec().metadata(metadata).spec(podSpec);

        int backoffLimit = Optional.ofNullable(runnable.getBackoffLimit()).orElse(3).intValue();

        // Create the JobSpec with the PodTemplateSpec
        V1JobSpec jobSpec = new V1JobSpec()
            .activeDeadlineSeconds(Long.valueOf(activeDeadlineSeconds))
            //TODO support work-queue style/parallel jobs
            .parallelism(1)
            .completions(1)
            .backoffLimit(backoffLimit)
            .template(podTemplateSpec);

        // Create the V1Job object with metadata and JobSpec
        return new V1Job().metadata(metadata).spec(jobSpec);
    }

    public V1Job apply(@NotNull V1Job job) throws K8sFrameworkException {
        Assert.notNull(job.getMetadata(), "metadata can not be null");

        try {
            // Log service execution initiation
            log.info("----------------- RUN KUBERNETES JOB ----------------");

            //dispatch job via api
            V1Job createdJob = batchV1Api.createNamespacedJob(namespace, job, null, null, null, null);
            log.info("Job created: {}", Objects.requireNonNull(createdJob.getMetadata()).getName());
            return createdJob;
        } catch (ApiException e) {
            log.error("Error with k8s: {}", e.getMessage());
            if (log.isDebugEnabled()) {
                log.debug("k8s api response: {}", e.getResponseBody());
            }

            throw new K8sFrameworkException(e.getMessage());
        }
    }

    public V1Job get(@NotNull V1Job job) throws K8sFrameworkException {
        Assert.notNull(job.getMetadata(), "metadata can not be null");

        try {
            // Log service execution initiation
            log.info("----------------- GET KUBERNETES JOB ----------------");

            return batchV1Api.readNamespacedJob(job.getMetadata().getName(), namespace, null);
        } catch (ApiException e) {
            log.error("Error with k8s: {}", e.getMessage());
            if (log.isDebugEnabled()) {
                log.debug("k8s api response: {}", e.getResponseBody());
            }

            throw new K8sFrameworkException(e.getMessage());
        }
    }

    private void monitor(K8sJobRunnable runnable, V1Job job) {
        // FIXME: DELETE THIS IS ONLY FOR DEBUG
        String threadName = Thread.currentThread().getName();

        // Generate jobName and ContainerName
        String jobName = k8sBuilderHelper.getJobName(runnable.getRuntime(), runnable.getTask(), runnable.getId());
        String containerName = k8sBuilderHelper.getContainerName(
            runnable.getRuntime(),
            runnable.getTask(),
            runnable.getId()
        );
        // Initialize the run state machine considering current state and context
        //TODO implement a dedicated poller
        StateMachine<RunState, RunEvent, Map<String, Object>> fsm = runStateMachine.create(
            RunState.valueOf(runnable.getState()),
            Map.of("runId", runnable.getId())
        );

        // Log the initiation of Dbt Kubernetes Listener
        log.info("Kubernetes Listener [" + threadName + "] " + jobName + "@" + namespace);

        // Define a function with parameters
        Function<
            String,
            Function<String, Function<StateMachine<RunState, RunEvent, Map<String, Object>>, Void>>
        > checkJobStatus = jName ->
            cName ->
                fMachine -> {
                    try {
                        V1Job v1Job = batchV1Api.readNamespacedJob(jName, namespace, null);
                        V1JobStatus v1JobStatus = v1Job.getStatus();

                        // Check the Job status
                        if (
                            Objects.requireNonNull(v1JobStatus).getSucceeded() != null &&
                            !fMachine.getCurrentState().equals(RunState.COMPLETED)
                        ) {
                            // Job has completed successfully
                            log.info("Job completed successfully.");
                            // Update state machine and update runDTO
                            fMachine.goToState(RunState.COMPLETED);
                            Run runDTO = runService.getRun(runnable.getId());
                            runDTO.getStatus().put("state", fsm.getCurrentState().name());
                            runService.updateRun(runDTO, runDTO.getId());

                            // Log pod status
                            logPod(jName, cName, namespace, runnable);
                            // Delete job and pod
                            deleteAssociatedPodAndJob(jName, namespace, runnable);
                        } else if (Objects.requireNonNull(v1JobStatus).getFailed() != null) {
                            // Job has failed delete job and pod
                            deleteAssociatedPodAndJob(jName, namespace, runnable);
                        } else if (v1JobStatus.getActive() != null && v1JobStatus.getActive() > 0) {
                            if (!fMachine.getCurrentState().equals(RunState.RUNNING)) {
                                fMachine.goToState(RunState.READY);
                                fMachine.goToState(RunState.RUNNING);
                            }
                            log.warn("Job is running...");
                            logPod(jName, cName, namespace, runnable);
                        } else {
                            String v1JobStatusString = JacksonMapper.CUSTOM_OBJECT_MAPPER.writeValueAsString(
                                v1JobStatus
                            );
                            log.warn("Job is in an unknown state : " + v1JobStatusString);
                            writeLog(runnable, v1JobStatusString);
                        }
                    } catch (ApiException | JsonProcessingException e) {
                        log.error("Error with k8s: {}", e.getMessage());

                        deleteAssociatedPodAndJob(jName, namespace, runnable);
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
                    .step((Function<?, ?>) i -> checkJobStatus.apply(jobName).apply(containerName).apply(fsm))
                    .build()
            ),
            1,
            true,
            false
        );

        // Start job poller
        pollingService.startOne(runnable.getId());
    }

    private void writeLog(K8sJobRunnable runnable, String log) {
        LogMetadata logMetadata = new LogMetadata();
        logMetadata.setProject(runnable.getProject());
        logMetadata.setRun(runnable.getId());
        Log logDTO = Log.builder().body(Map.of("content", log)).metadata(logMetadata.toMap()).build();
        logService.createLog(logDTO);
    }

    /**
     * Logging pod
     *
     * @param jobName  the name of the Job
     * @param runnable the runnable Type in this case K8SJobRunnable
     */
    private void logPod(String jobName, String cName, String namespace, K8sJobRunnable runnable) {
        try {
            // Retrieve and print the logs of the associated Pod
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
                        String logs = coreV1Api.readNamespacedPodLog(
                            podName,
                            namespace,
                            cName,
                            false,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null
                        );

                        log.info("Logs for Pod: " + podName);
                        log.info("Log is: " + logs);
                        if (logs != null) writeLog(runnable, logs);
                    }
                }
            }
        } catch (ApiException e) {
            log.error(e.getResponseBody());
            //throw new RuntimeException(e);
        }
    }

    /**
     * Delete job
     *
     * @param jobName  the name of the Job
     * @param runnable the runnable Type in this case K8SJobRunnable
     */
    private void deleteAssociatedPodAndJob(String jobName, String namespace, K8sJobRunnable runnable) {
        // Delete the Pod associated with the Job
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

                        // Delete the Job
                        V1Status deleteStatus = batchV1Api.deleteNamespacedJob(
                            jobName,
                            "default",
                            null,
                            null,
                            null,
                            null,
                            null,
                            null
                        );

                        try {
                            writeLog(runnable, JacksonMapper.CUSTOM_OBJECT_MAPPER.writeValueAsString(deleteStatus));
                        } catch (JsonProcessingException e) {
                            log.error(e.toString());
                        }
                        log.info("Job deleted: " + jobName);
                    }
                }
            }
            throw new StopPoller("POLLER STOP SUCCESSFULLY");
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }
}
