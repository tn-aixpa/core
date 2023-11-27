package it.smartcommunitylabdhub.core.components.infrastructure.frameworks;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import it.smartcommunitylabdhub.core.annotations.infrastructure.FrameworkComponent;
import it.smartcommunitylabdhub.core.components.fsm.StateMachine;
import it.smartcommunitylabdhub.core.components.fsm.enums.RunEvent;
import it.smartcommunitylabdhub.core.components.fsm.enums.RunState;
import it.smartcommunitylabdhub.core.components.fsm.types.RunStateMachine;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.frameworks.Framework;
import it.smartcommunitylabdhub.core.components.infrastructure.runnables.K8sJobRunnable;
import it.smartcommunitylabdhub.core.components.kubernetes.K8sJobBuilderHelper;
import it.smartcommunitylabdhub.core.components.pollers.PollingService;
import it.smartcommunitylabdhub.core.components.workflows.factory.WorkflowFactory;
import it.smartcommunitylabdhub.core.exceptions.CoreException;
import it.smartcommunitylabdhub.core.exceptions.StopPoller;
import it.smartcommunitylabdhub.core.models.builders.log.LogEntityBuilder;
import it.smartcommunitylabdhub.core.models.entities.log.LogDTO;
import it.smartcommunitylabdhub.core.models.entities.run.Run;
import it.smartcommunitylabdhub.core.services.interfaces.LogService;
import it.smartcommunitylabdhub.core.services.interfaces.RunService;
import it.smartcommunitylabdhub.core.utils.ErrorList;
import it.smartcommunitylabdhub.core.utils.JacksonMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.function.TriFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

import java.util.*;
import java.util.stream.Stream;

@Slf4j
@FrameworkComponent(framework = "k8sjob")
public class K8sJobFramework implements Framework<K8sJobRunnable> {

    @Autowired
    BatchV1Api batchV1Api;

    @Autowired
    CoreV1Api coreV1Api;

    @Autowired
    PollingService pollingService;

    @Autowired
    RunStateMachine runStateMachine;

    @Autowired
    LogEntityBuilder logEntityBuilder;

    @Autowired
    LogService logService;

    @Autowired
    RunService runService;

    @Autowired
    K8sJobBuilderHelper k8sJobBuilderHelper;


    @Value("${kubernetes.namespace}")
    private String namespace;


    // TODO: instead of void define a Result object that have to be merged with the run from the
    // caller.
    @Override
    public void execute(K8sJobRunnable runnable) throws CoreException {
        // FIXME: DELETE THIS IS ONLY FOR DEBUG
        String threadName = Thread.currentThread().getName();
        //String placeholder = "-" + RandomStringGenerator.generateRandomString(3);

        // Log service execution initiation
        log.info("----------------- PREPARE KUBERNETES JOB ----------------");

        // Generate jobName and ContainerName
        String jobName = getJobName(
                runnable.getRuntime(),
                runnable.getTask(),
                runnable.getId()
        );

        String containerName = getContainerName(
                runnable.getRuntime(),
                runnable.getTask(),
                runnable.getId()
        );

        // Create labels for job
        Map<String, String> labels = Map.of(
                "app.kubernetes.io/instance", "dhcore-" + jobName,
                "app.kubernetes.io/version", "0.0.3",
                "app.kubernetes.io/component", "job",
                "app.kubernetes.io/part-of", "dhcore-k8sjob",
                "app.kubernetes.io/managed-by", "dhcore");


        // Prepare environment variables for the Kubernetes job
        List<V1EnvVar> envVars = k8sJobBuilderHelper.getEnvV1();

        // Merge function specific envs
        runnable.getEnvs().forEach((key, value) -> envVars.add(
                new V1EnvVar().name(key).value(value)));

        // Build Container
        V1Container container = new V1Container()
                .name(containerName)
                .image(runnable.getImage())
                .command(getCommand(runnable))
                .imagePullPolicy("IfNotPresent")
                .env(envVars);

        // Create a PodSpec for the container
        V1PodSpec podSpec = new V1PodSpec()
                .containers(Collections.singletonList(container))
                .restartPolicy("Never");

        // Create a PodTemplateSpec with the PodSpec
        V1PodTemplateSpec podTemplateSpec = new V1PodTemplateSpec()
                .spec(podSpec);

        // Create the JobSpec with the PodTemplateSpec
        V1JobSpec jobSpec = new V1JobSpec()
                // .completions(1)
                // .backoffLimit(6)    // is the default value
                .template(podTemplateSpec);

        // Create the Job metadata
        V1ObjectMeta metadata = new V1ObjectMeta()
                .name(jobName)
                .labels(labels);


        // Create the V1Job object with metadata and JobSpec
        V1Job job = new V1Job()
                .metadata(metadata)
                .spec(jobSpec);

        try {
            V1Job createdJob = batchV1Api.createNamespacedJob(namespace, job, null, null, null, null);
            log.info("Job created: " + Objects.requireNonNull(createdJob.getMetadata()).getName());
        } catch (Exception e) {
            log.error("====== K8s FATAL ERROR =====");
            log.error(String.valueOf(e));
            // Handle exceptions here
            throw new CoreException(
                    ErrorList.RUN_JOB_ERROR.getValue(),
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }


        // TODO: change this part as a poller instead of a watcher using kubeclient and jobId
        // Initialize the run state machine considering current state and context
        StateMachine<RunState, RunEvent, Map<String, Object>> fsm = runStateMachine
                .create(RunState.valueOf(runnable.getState()),
                        Map.of("runId", runnable.getId()));


        // Log the initiation of Dbt Kubernetes Listener
        log.info("Dbt Kubernetes Listener [" + threadName + "] "
                + jobName
                + "@"
                + namespace);


        // Define a function with parameters
        TriFunction<String, String,
                StateMachine<
                        RunState,
                        RunEvent,
                        Map<String, Object>>,
                Void> checkJobStatus = (jName, cName, fMachine) -> {
            try {
                V1Job v1Job = batchV1Api.readNamespacedJob(jName, namespace, null);
                V1JobStatus v1JobStatus = v1Job.getStatus();

                // Check the Job status
                if (Objects.requireNonNull(v1JobStatus).getSucceeded() != null
                        && !fMachine.getCurrentState().equals(RunState.COMPLETED)) {


                    // Job has completed successfully
                    log.info("Job completed successfully.");
                    // Update state machine and update runDTO
                    fMachine.goToState(RunState.COMPLETED);
                    Run runDTO = runService.getRun(runnable.getId());
                    runDTO.setState(fsm.getCurrentState().name());
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
                    String v1JobStatusString = JacksonMapper.objectMapper.writeValueAsString(v1JobStatus);
                    log.warn("Job is in an unknown state : " + v1JobStatusString);
//                    writeLog(runnable, v1JobStatusString);
                }

            } catch (ApiException | JsonProcessingException | CoreException e) {
                deleteAssociatedPodAndJob(jName, namespace, runnable);
                throw new StopPoller(e.getMessage());
            }

            return null;
        };

        // Using the step method with explicit arguments
        pollingService.createPoller(jobName, List.of(
                WorkflowFactory.builder().step(checkJobStatus, jobName, containerName, fsm).build()
        ), 1, true, false);

        // Start job poller
        pollingService.startOne(jobName);
    }


    private void writeLog(K8sJobRunnable runnable, String log) {
        logService.createLog(LogDTO.builder()
                .run(runnable.getId())
                .project(runnable.getProject())
                .body(Map.of("content", log))
                .build());
    }

    // Concat command with arguments
    private List<String> getCommand(K8sJobRunnable runnable) {
        return List.of(Stream.concat(
                Stream.of(runnable.getCommand()),
                Arrays.stream(runnable.getArgs())).toArray(String[]::new));
    }

    // Generate and return job name
    private String getJobName(String runtime, String task, String id) {
        return "j" + "-" + runtime + "-" + task + "-" + id;
    }

    // Generate and return container name
    private String getContainerName(String runtime, String task, String id) {
        return "c" + "-" + runtime + "-" + task + "-" + id;
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
                    namespace, null,
                    null, null,
                    null, null,
                    null, null,
                    null, null,
                    null, null);

            for (V1Pod pod : v1PodList.getItems()) {
                if (pod.getMetadata() != null && pod.getMetadata().getName() != null) {
                    if (pod.getMetadata().getName().startsWith(jobName)) {
                        String podName = pod.getMetadata().getName();
                        String logs = coreV1Api.readNamespacedPodLog(podName, namespace, cName,
                                false, null,
                                null, null,
                                null, null,
                                null, null);


                        log.info("Logs for Pod: " + podName);
                        log.info("Log is: " + logs);
//                        if (logs != null)
//                            writeLog(runnable, logs);
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
                    namespace, null,
                    null, null,
                    null, null,
                    null, null,
                    null, null,
                    null, null);


            for (V1Pod pod : v1PodList.getItems()) {
                if (pod.getMetadata() != null && pod.getMetadata().getName() != null) {
                    if (pod.getMetadata().getName().startsWith(jobName)) {
//                        String podName = pod.getMetadata().getName();
//
//                        // Delete the Pod
//                        V1Pod v1Pod = coreV1Api.deleteNamespacedPod(podName, namespace, null,
//                                null, null,
//                                null, null,
//                                null);
//                        log.info("Pod deleted: " + podName);
////                        writeLog(runnable, JacksonMapper.objectMapper.writeValueAsString(v1Pod.getStatus()));
//
//                        // Delete the Job
//                        V1Status deleteStatus = batchV1Api.deleteNamespacedJob(
//                                jobName, "default", null,
//                                null, null, null,
//                                null, null);
//
////                        writeLog(runnable, JacksonMapper.objectMapper.writeValueAsString(deleteStatus));
//                        log.info("Job deleted: " + jobName);
                    }
                }
            }
            throw new StopPoller("POLLER STOP SUCCESSFULLY");
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

}
