package it.smartcommunitylabdhub.framework.k8s.infrastructure.monitor;

import io.kubernetes.client.openapi.models.V1Job;
import it.smartcommunitylabdhub.commons.annotations.infrastructure.MonitorComponent;
import it.smartcommunitylabdhub.commons.events.RunnableChangedEvent;
import it.smartcommunitylabdhub.commons.events.RunnableMonitorObject;
import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.services.RunnableStore;
import it.smartcommunitylabdhub.framework.k8s.annotations.ConditionalOnKubernetes;
import it.smartcommunitylabdhub.framework.k8s.exceptions.K8sFrameworkException;
import it.smartcommunitylabdhub.framework.k8s.infrastructure.k8s.K8sJobFramework;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sJobRunnable;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.Assert;

@Slf4j
@ConditionalOnKubernetes
@MonitorComponent(framework = "job")
public class K8sJobMonitor implements K8sBaseMonitor<Void> {

    private final K8sJobFramework k8sJobFramework;
    private final RunnableStore<K8sJobRunnable> runnableStore;
    private final ApplicationEventPublisher eventPublisher;

    public K8sJobMonitor(
        K8sJobFramework k8sJobFramework,
        RunnableStore<K8sJobRunnable> runnableStore,
        ApplicationEventPublisher eventPublisher
    ) {
        this.k8sJobFramework = k8sJobFramework;
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
                    V1Job v1Job = k8sJobFramework.get(k8sJobFramework.build(runnable));
                    Assert.notNull(Objects.requireNonNull(v1Job.getStatus()), "Job status can not be null");
                    log.info("Job status: {}", v1Job.getStatus().toString());

                    if (v1Job.getStatus().getSucceeded() != null) {
                        // Job has succeeded
                        runnable.setState(State.COMPLETED.name());
                    } else if (v1Job.getStatus().getFailed() != null) {
                        // Job has failed delete job and pod
                        runnable.setState(State.ERROR.name());
                    } else if (v1Job.getStatus().getActive() != null && v1Job.getStatus().getActive() > 0) {
                        // Job is active and is running
                        runnable.setState(State.RUNNING.name());
                    }

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

    private void monitor(K8sJobRunnable runnable, V1Job job) {
        //        // FIXME: DELETE THIS IS ONLY FOR DEBUG
        //        String threadName = Thread.currentThread().getName();
        //
        //        // Generate jobName and ContainerName
        //        String jobName = k8sBuilderHelper.getJobName(runnable.getRuntime(), runnable.getTask(), runnable.getId());
        //        String containerName = k8sBuilderHelper.getContainerName(
        //                runnable.getRuntime(),
        //                runnable.getTask(),
        //                runnable.getId()
        //        );
        //        // Initialize the run state machine considering current state and context
        //        //TODO implement a dedicated poller
        //        StateMachine<RunState, RunEvent, Map<String, Object>> fsm = runStateMachine.create(
        //                RunState.valueOf(runnable.getState()),
        //                Map.of("runId", runnable.getId())
        //        );
        //
        //        // Log the initiation of Dbt Kubernetes Listener
        //        log.info("Kubernetes Listener [" + threadName + "] " + jobName + "@" + namespace);
        //
        //        // Define a function with parameters
        //        Function<
        //                String,
        //                Function<String, Function<StateMachine<RunState, RunEvent, Map<String, Object>>, Void>>
        //                > checkJobStatus = jName ->
        //                cName ->
        //                        fMachine -> {
        //                            try {
        //                                V1Job v1Job = batchV1Api.readNamespacedJob(jName, namespace, null);
        //                                V1JobStatus v1JobStatus = v1Job.getStatus();
        //
        //                                // Check the Job status
        //                                if (
        //                                        Objects.requireNonNull(v1JobStatus).getSucceeded() != null &&
        //                                                !fMachine.getCurrentState().equals(RunState.COMPLETED)
        //                                ) {
        //                                    // Job has completed successfully
        //                                    log.info("Job completed successfully.");
        //                                    // Update state machine and update runDTO
        //                                    fMachine.goToState(RunState.COMPLETED);
        //                                    Run runDTO = runService.getRun(runnable.getId());
        //                                    runDTO.getStatus().put("state", fsm.getCurrentState().name());
        //                                    runService.updateRun(runDTO, runDTO.getId());
        //
        //                                    // Log pod status
        //                                    logPod(jName, cName, namespace, runnable);
        //                                    // Delete job and pod
        //                                    deleteAssociatedPodAndJob(jName, namespace, runnable);
        //                                } else if (Objects.requireNonNull(v1JobStatus).getFailed() != null) {
        //                                    // Job has failed delete job and pod
        //                                    deleteAssociatedPodAndJob(jName, namespace, runnable);
        //                                } else if (v1JobStatus.getActive() != null && v1JobStatus.getActive() > 0) {
        //                                    if (!fMachine.getCurrentState().equals(RunState.RUNNING)) {
        //                                        fMachine.goToState(RunState.READY);
        //                                        fMachine.goToState(RunState.RUNNING);
        //                                    }
        //                                    log.warn("Job is running...");
        //                                    logPod(jName, cName, namespace, runnable);
        //                                } else {
        //                                    String v1JobStatusString = JacksonMapper.CUSTOM_OBJECT_MAPPER.writeValueAsString(
        //                                            v1JobStatus
        //                                    );
        //                                    log.warn("Job is in an unknown state : " + v1JobStatusString);
        //                                    writeLog(runnable, v1JobStatusString);
        //                                }
        //                            } catch (ApiException | JsonProcessingException e) {
        //                                log.error("Error with k8s: {}", e.getMessage());
        //
        //                                deleteAssociatedPodAndJob(jName, namespace, runnable);
        //                                throw new StopPoller(e.getMessage());
        //                            }
        //
        //                            return null;
        //                        };
        //
        //        // Using the step method with explicit arguments
        //        pollingService.createPoller(
        //                runnable.getId(),
        //                List.of(
        //                        WorkflowFactory
        //                                .builder()
        //                                .step(i -> checkJobStatus.apply(jobName).apply(containerName).apply(fsm))
        //                                .build()
        //                ),
        //                1,
        //                true,
        //                false
        //        );
        //
        //        // Start job poller
        //        pollingService.startOne(runnable.getId());
    }
}
