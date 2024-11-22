package it.smartcommunitylabdhub.core.components.run;

import it.smartcommunitylabdhub.authorization.services.JwtTokenService;
import it.smartcommunitylabdhub.commons.accessors.fields.StatusFieldAccessor;
import it.smartcommunitylabdhub.commons.accessors.spec.RunSpecAccessor;
import it.smartcommunitylabdhub.commons.accessors.spec.TaskSpecAccessor;
import it.smartcommunitylabdhub.commons.config.SecurityProperties;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import it.smartcommunitylabdhub.commons.exceptions.SystemException;
import it.smartcommunitylabdhub.commons.infrastructure.RunRunnable;
import it.smartcommunitylabdhub.commons.infrastructure.Runtime;
import it.smartcommunitylabdhub.commons.infrastructure.SecuredRunnable;
import it.smartcommunitylabdhub.commons.models.base.Executable;
import it.smartcommunitylabdhub.commons.models.base.ExecutableBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.models.run.Run;
import it.smartcommunitylabdhub.commons.models.run.RunBaseSpec;
import it.smartcommunitylabdhub.commons.models.run.RunBaseStatus;
import it.smartcommunitylabdhub.commons.models.task.Task;
import it.smartcommunitylabdhub.commons.services.FunctionService;
import it.smartcommunitylabdhub.commons.services.RunService;
import it.smartcommunitylabdhub.commons.services.WorkflowService;
import it.smartcommunitylabdhub.commons.utils.MapUtils;
import it.smartcommunitylabdhub.core.components.infrastructure.processors.ProcessorRegistry;
import it.smartcommunitylabdhub.core.components.infrastructure.runtimes.RuntimeFactory;
import it.smartcommunitylabdhub.core.fsm.RunEvent;
import it.smartcommunitylabdhub.core.fsm.RunStateMachineFactory;
import it.smartcommunitylabdhub.core.models.entities.RunEntity;
import it.smartcommunitylabdhub.core.models.entities.TaskEntity;
import it.smartcommunitylabdhub.core.services.EntityService;
import it.smartcommunitylabdhub.fsm.Fsm;
import it.smartcommunitylabdhub.fsm.exceptions.InvalidTransitionException;
import it.smartcommunitylabdhub.runtimes.events.RunnableChangedEvent;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.util.Pair;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RunManager {

    // public static final int DEFAULT_TIMEOUT = 30;

    // @Autowired
    // private RunStateMachineFactory runStateMachine;

    // @Autowired
    // private EntityService<Run, RunEntity> entityService;

    // @Autowired
    // private EntityService<Task, TaskEntity> taskEntityService;

    // @Autowired
    // private FunctionService functionService;

    // @Autowired
    // private WorkflowService workflowService;

    // // @Autowired
    // // private ExecutableEntityService executableEntityServiceProvider;

    // @Autowired
    // private RunService runService;

    // @Autowired
    // private RuntimeFactory runtimeFactory;

    // @Autowired
    // private ApplicationEventPublisher eventPublisher;

    // @Autowired
    // ProcessorRegistry processorRegistry;

    // @Autowired
    // JwtTokenService jwtTokenService;

    // @Autowired
    // SecurityProperties securityProperties;

    // private Map<String, Pair<ReentrantLock, Instant>> locks = new ConcurrentHashMap<>();
    // private int timeout = DEFAULT_TIMEOUT;

    // /*
    //  * Locking
    //  */
    // public void setTimeout(int t) {
    //     this.timeout = t;
    // }

    // private synchronized ReentrantLock getLock(String id) {
    //     //build lock
    //     ReentrantLock l = locks.containsKey(id) ? locks.get(id).getFirst() : new ReentrantLock();

    //     //update last used date
    //     locks.put(id, Pair.of(l, Instant.now()));

    //     return l;
    // }

    // /*
    //  * Actions
    //  * TODO refactor from a factory!
    //  */

    // public Run build(@NotNull Run run) throws NoSuchEntityException {
    //     log.debug("build run with id {}", run.getId());
    //     if (log.isTraceEnabled()) {
    //         log.trace("run: {}", run);
    //     }

    //     try {
    //         String id = run.getId();

    //         //acquire write lock
    //         getLock(id).tryLock(timeout, TimeUnit.SECONDS);

    //         try {
    //             // Read spec and retrieve executables
    //             RunSpecAccessor runSpecAccessor = RunSpecAccessor.with(run.getSpec());
    //             Task task = taskEntityService.get(runSpecAccessor.getTaskId());

    //             //resolve runtime either from runSpec or from taskSpec
    //             String runtime = runSpecAccessor.getRuntime() != null
    //                 ? runSpecAccessor.getRuntime()
    //                 : TaskSpecAccessor.with(task.getSpec()).getRuntime();

    //             // Retrieve state machine
    //             Fsm<State, RunEvent, Map<String, Serializable>> fsm = createFsm(run);

    //             // Add Internal logic to be executed when state change from CREATED to READY
    //             fsm
    //                 .getState(State.CREATED)
    //                 .getTransition(RunEvent.BUILD)
    //                 .setInternalLogic((context, input, fsmInstance) -> {
    //                     if (!runSpecAccessor.isLocalExecution()) {
    //                         // Retrieve Runtime and build run
    //                         Runtime<
    //                             ? extends ExecutableBaseSpec,
    //                             ? extends RunBaseSpec,
    //                             ? extends RunBaseStatus,
    //                             ? extends RunRunnable
    //                         > r = runtimeFactory.getRuntime(runtime);

    //                         Executable function = runSpecAccessor.getWorkflowId() != null
    //                             ? workflowService.getWorkflow(runSpecAccessor.getWorkflowId())
    //                             : functionService.getFunction(runSpecAccessor.getFunctionId());

    //                         RunBaseSpec runSpecBuilt = r.build(function, task, run);

    //                         return Optional.of(runSpecBuilt);
    //                     }
    //                     return Optional.empty();
    //                 });

    //             try {
    //                 // Update run state to BUILT
    //                 Optional<RunBaseSpec> runSpecBuilt = fsm.goToState(State.BUILT, null);
    //                 runSpecBuilt.ifPresent(spec -> {
    //                     // Update run spec
    //                     run.setSpec(spec.toMap());

    //                     // Update run state to BUILT
    //                     RunBaseStatus runBaseStatus = RunBaseStatus.with(run.getStatus());
    //                     runBaseStatus.setState(State.BUILT.toString());

    //                     // Iterate over all processor and store all RunBaseStatus as optional
    //                     List<RunBaseStatus> processorsStatus = processorRegistry
    //                         .getProcessors("onBuilt")
    //                         .stream()
    //                         .map(processor -> processor.process(run, null, runBaseStatus))
    //                         .filter(Objects::nonNull)
    //                         .collect(Collectors.toList());

    //                     Map<String, Serializable> runStatusMap = processorsStatus
    //                         .stream()
    //                         .map(RunBaseStatus::toMap)
    //                         .reduce(new HashMap<>(), MapUtils::mergeMultipleMaps);

    //                     run.setStatus(MapUtils.mergeMultipleMaps(run.getStatus(), runBaseStatus.toMap(), runStatusMap));

    //                     if (log.isTraceEnabled()) {
    //                         log.trace("Built run: {}", run);
    //                     }
    //                 });

    //                 return entityService.update(run.getId(), run);
    //             } catch (InvalidTransitionException e) {
    //                 // log error
    //                 log.debug("Invalid transaction from state {}  to state {}", e.getFromState(), e.getToState());
    //                 throw e;
    //             }
    //         } catch (StoreException e) {
    //             log.error("store error: {}", e.getMessage());
    //             throw new SystemException(e.getMessage());
    //         } finally {
    //             getLock(id).unlock();
    //         }
    //     } catch (InterruptedException e) {
    //         throw new SystemException("unable to acquire lock: " + e.getMessage());
    //     }
    // }

    // public Run run(@NotNull Run run) throws NoSuchEntityException, InvalidTransitionException {
    //     log.debug("run run with id {}", run.getId());
    //     if (log.isTraceEnabled()) {
    //         log.trace("run: {}", run);
    //     }

    //     try {
    //         String id = run.getId();

    //         //acquire write lock
    //         getLock(id).tryLock(timeout, TimeUnit.SECONDS);

    //         try {
    //             // Read spec and retrieve executables
    //             RunSpecAccessor runSpecAccessor = RunSpecAccessor.with(run.getSpec());

    //             //resolve runtime either from runSpec or from taskSpec
    //             String runtime = runSpecAccessor.getRuntime() != null
    //                 ? runSpecAccessor.getRuntime()
    //                 : TaskSpecAccessor.with(taskEntityService.get(runSpecAccessor.getTaskId()).getSpec()).getRuntime();

    //             // Retrieve state machine
    //             Fsm<State, RunEvent, Map<String, Serializable>> fsm = createFsm(run);

    //             fsm
    //                 .getState(State.BUILT)
    //                 .getTransition(RunEvent.RUN)
    //                 .setInternalLogic((context, input, stateMachine) -> {
    //                     if (!runSpecAccessor.isLocalExecution()) {
    //                         // Create Runnable
    //                         RunRunnable runnable = runtimeFactory.getRuntime(runtime).run(run);
    //                         return Optional.of(runnable);
    //                     } else {
    //                         return Optional.empty();
    //                     }
    //                 });

    //             try {
    //                 Optional<RunRunnable> runnable = fsm.goToState(State.READY, null);

    //                 runnable.ifPresent(r -> {
    //                     //extract auth from security context to inflate secured credentials
    //                     //TODO refactor properly
    //                     if (r instanceof SecuredRunnable) {
    //                         // check that auth is enabled via securityProperties
    //                         Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    //                         if (auth != null && securityProperties.isRequired()) {
    //                             Serializable credentials = jwtTokenService.generateCredentials(auth);
    //                             if (credentials != null) {
    //                                 ((SecuredRunnable) r).setCredentials(credentials);
    //                             }
    //                         }
    //                     }

    //                     // Dispatch Runnable event to specific event listener es (serve,job,deploy...)
    //                     eventPublisher.publishEvent(r);

    //                     // Update run state to READY
    //                     RunBaseStatus runBaseStatus = RunBaseStatus.with(run.getStatus());
    //                     runBaseStatus.setState(State.READY.toString());
    //                     runBaseStatus.setMessage(r.getMessage());

    //                     // Iterate over all processor and store all RunBaseStatus as optional
    //                     List<RunBaseStatus> processorsStatus = processorRegistry
    //                         .getProcessors("onReady")
    //                         .stream()
    //                         .map(processor -> processor.process(run, r, runBaseStatus))
    //                         .filter(Objects::nonNull)
    //                         .collect(Collectors.toList());

    //                     Map<String, Serializable> runStatusMap = processorsStatus
    //                         .stream()
    //                         .map(RunBaseStatus::toMap)
    //                         .reduce(new HashMap<>(), MapUtils::mergeMultipleMaps);

    //                     run.setStatus(MapUtils.mergeMultipleMaps(run.getStatus(), runBaseStatus.toMap(), runStatusMap));
    //                 });

    //                 return entityService.update(run.getId(), run);
    //             } catch (InvalidTransitionException e) {
    //                 // log error
    //                 log.debug("Invalid transaction from state {}  to state {}", e.getFromState(), e.getToState());
    //                 throw e;
    //             }
    //         } catch (StoreException e) {
    //             log.error("store error: {}", e.getMessage());
    //             throw new SystemException(e.getMessage());
    //         } finally {
    //             getLock(id).unlock();
    //         }
    //     } catch (InterruptedException e) {
    //         throw new SystemException("unable to acquire lock: " + e.getMessage());
    //     }
    // }

    // public Run stop(@NotNull Run run) throws NoSuchEntityException {
    //     log.debug("stop run with id {}", run.getId());
    //     if (log.isTraceEnabled()) {
    //         log.trace("run: {}", run);
    //     }

    //     try {
    //         String id = run.getId();

    //         //acquire write lock
    //         getLock(id).tryLock(timeout, TimeUnit.SECONDS);

    //         try {
    //             // Read spec and retrieve executables
    //             RunSpecAccessor runSpecAccessor = RunSpecAccessor.with(run.getSpec());

    //             //resolve runtime either from runSpec or from taskSpec
    //             String runtime = runSpecAccessor.getRuntime() != null
    //                 ? runSpecAccessor.getRuntime()
    //                 : TaskSpecAccessor.with(taskEntityService.get(runSpecAccessor.getTaskId()).getSpec()).getRuntime();

    //             // Retrieve state machine
    //             Fsm<State, RunEvent, Map<String, Serializable>> fsm = createFsm(run);

    //             fsm
    //                 .getState(State.RUNNING)
    //                 .getTransition(RunEvent.STOP)
    //                 .setInternalLogic((context, input, stateMachine) -> {
    //                     if (!runSpecAccessor.isLocalExecution()) {
    //                         // Create Runnable
    //                         RunRunnable runnable = runtimeFactory.getRuntime(runtime).stop(run);
    //                         return Optional.of(runnable);
    //                     } else {
    //                         return Optional.empty();
    //                     }
    //                 });

    //             try {
    //                 Optional<RunRunnable> runnable = fsm.goToState(State.STOP, null);
    //                 runnable.ifPresent(r -> {
    //                     // Dispatch Runnable event to specific event listener es (serve,job,deploy...)
    //                     eventPublisher.publishEvent(r);

    //                     // Update run state to STOP
    //                     RunBaseStatus runBaseStatus = RunBaseStatus.with(run.getStatus());
    //                     runBaseStatus.setState(State.STOP.toString());
    //                     runBaseStatus.setMessage(r.getMessage());

    //                     // Iterate over all processor and store all RunBaseStatus as optional
    //                     List<RunBaseStatus> processorsStatus = processorRegistry
    //                         .getProcessors("onStopping")
    //                         .stream()
    //                         .map(processor -> processor.process(run, r, runBaseStatus))
    //                         .filter(Objects::nonNull)
    //                         .collect(Collectors.toList());

    //                     Map<String, Serializable> runStatusMap = processorsStatus
    //                         .stream()
    //                         .map(RunBaseStatus::toMap)
    //                         .reduce(new HashMap<>(), MapUtils::mergeMultipleMaps);

    //                     run.setStatus(MapUtils.mergeMultipleMaps(run.getStatus(), runBaseStatus.toMap(), runStatusMap));
    //                 });

    //                 return entityService.update(run.getId(), run);
    //             } catch (InvalidTransitionException e) {
    //                 // log error
    //                 log.debug("Invalid transaction from state {}  to state {}", e.getFromState(), e.getToState());
    //                 throw e;
    //             }
    //         } catch (StoreException e) {
    //             log.error("store error: {}", e.getMessage());
    //             throw new SystemException(e.getMessage());
    //         } finally {
    //             getLock(id).unlock();
    //         }
    //     } catch (InterruptedException e) {
    //         throw new SystemException("unable to acquire lock: " + e.getMessage());
    //     }
    // }

    // public Run resume(@NotNull Run run) throws NoSuchEntityException {
    //     log.debug("resume run with id {}", run.getId());
    //     if (log.isTraceEnabled()) {
    //         log.trace("run: {}", run);
    //     }

    //     try {
    //         String id = run.getId();

    //         //acquire write lock
    //         getLock(id).tryLock(timeout, TimeUnit.SECONDS);

    //         try {
    //             // Read spec and retrieve executables
    //             RunSpecAccessor runSpecAccessor = RunSpecAccessor.with(run.getSpec());

    //             //resolve runtime either from runSpec or from taskSpec
    //             String runtime = runSpecAccessor.getRuntime() != null
    //                 ? runSpecAccessor.getRuntime()
    //                 : TaskSpecAccessor.with(taskEntityService.get(runSpecAccessor.getTaskId()).getSpec()).getRuntime();

    //             // Retrieve state machine
    //             Fsm<State, RunEvent, Map<String, Serializable>> fsm = createFsm(run);

    //             fsm
    //                 .getState(State.STOPPED)
    //                 .getTransition(RunEvent.RESUME)
    //                 .setInternalLogic((context, input, stateMachine) -> {
    //                     if (!runSpecAccessor.isLocalExecution()) {
    //                         RunRunnable runnable = runtimeFactory.getRuntime(runtime).resume(run);
    //                         return Optional.of(runnable);
    //                     } else {
    //                         return Optional.empty();
    //                     }
    //                 });

    //             try {
    //                 Optional<RunRunnable> runnable = fsm.goToState(State.RESUME, null);
    //                 runnable.ifPresent(r -> {
    //                     // Dispatch Runnable event to specific event listener es (serve,job,deploy...)
    //                     eventPublisher.publishEvent(r);

    //                     // Update run state to RESUME
    //                     RunBaseStatus runBaseStatus = RunBaseStatus.with(run.getStatus());
    //                     runBaseStatus.setState(State.RESUME.toString());
    //                     runBaseStatus.setMessage(r.getMessage());

    //                     // Iterate over all processor and store all RunBaseStatus as optional
    //                     List<RunBaseStatus> processorsStatus = processorRegistry
    //                         .getProcessors("onResuming")
    //                         .stream()
    //                         .map(processor -> processor.process(run, r, runBaseStatus))
    //                         .filter(Objects::nonNull)
    //                         .collect(Collectors.toList());

    //                     Map<String, Serializable> runStatusMap = processorsStatus
    //                         .stream()
    //                         .map(RunBaseStatus::toMap)
    //                         .reduce(new HashMap<>(), MapUtils::mergeMultipleMaps);

    //                     run.setStatus(MapUtils.mergeMultipleMaps(run.getStatus(), runBaseStatus.toMap(), runStatusMap));
    //                 });

    //                 return entityService.update(run.getId(), run);
    //             } catch (InvalidTransitionException e) {
    //                 // log error
    //                 log.debug("Invalid transaction from state {}  to state {}", e.getFromState(), e.getToState());
    //                 throw e;
    //             }
    //         } catch (StoreException e) {
    //             log.error("store error: {}", e.getMessage());
    //             throw new SystemException(e.getMessage());
    //         } finally {
    //             getLock(id).unlock();
    //         }
    //     } catch (InterruptedException e) {
    //         throw new SystemException("unable to acquire lock: " + e.getMessage());
    //     }
    // }

    // public Run delete(@NotNull Run run) throws NoSuchEntityException {
    //     log.debug("delete run with id {}", run.getId());
    //     if (log.isTraceEnabled()) {
    //         log.trace("run: {}", run);
    //     }

    //     try {
    //         String id = run.getId();

    //         //acquire write lock
    //         getLock(id).tryLock(timeout, TimeUnit.SECONDS);

    //         try {
    //             // Read spec and retrieve executables
    //             RunSpecAccessor runSpecAccessor = RunSpecAccessor.with(run.getSpec());

    //             //resolve runtime either from runSpec or from taskSpec
    //             String runtime = runSpecAccessor.getRuntime() != null
    //                 ? runSpecAccessor.getRuntime()
    //                 : TaskSpecAccessor.with(taskEntityService.get(runSpecAccessor.getTaskId()).getSpec()).getRuntime();

    //             // Retrieve state machine
    //             Fsm<State, RunEvent, Map<String, Serializable>> fsm = createFsm(run);

    //             fsm
    //                 .getState(State.RUNNING)
    //                 .getTransition(RunEvent.DELETING)
    //                 .setInternalLogic((context, input, stateMachine) -> {
    //                     if (!runSpecAccessor.isLocalExecution()) {
    //                         RunRunnable runnable = runtimeFactory.getRuntime(runtime).delete(run);
    //                         return Optional.ofNullable(runnable);
    //                     } else {
    //                         return Optional.empty();
    //                     }
    //                 });
    //             fsm
    //                 .getState(State.STOPPED)
    //                 .getTransition(RunEvent.DELETING)
    //                 .setInternalLogic((context, input, stateMachine) -> {
    //                     if (!runSpecAccessor.isLocalExecution()) {
    //                         RunRunnable runnable = runtimeFactory.getRuntime(runtime).delete(run);
    //                         return Optional.ofNullable(runnable);
    //                     } else {
    //                         return Optional.empty();
    //                     }
    //                 });
    //             fsm
    //                 .getState(State.ERROR)
    //                 .getTransition(RunEvent.DELETING)
    //                 .setInternalLogic((context, input, stateMachine) -> {
    //                     if (!runSpecAccessor.isLocalExecution()) {
    //                         RunRunnable runnable = runtimeFactory.getRuntime(runtime).delete(run);
    //                         return Optional.ofNullable(runnable);
    //                     } else {
    //                         return Optional.empty();
    //                     }
    //                 });

    //             fsm
    //                 .getState(State.COMPLETED)
    //                 .getTransition(RunEvent.DELETING)
    //                 .setInternalLogic((context, input, stateMachine) -> {
    //                     if (!runSpecAccessor.isLocalExecution()) {
    //                         RunRunnable runnable = runtimeFactory.getRuntime(runtime).delete(run);
    //                         return Optional.ofNullable(runnable);
    //                     } else {
    //                         return Optional.empty();
    //                     }
    //                 });

    //             try {
    //                 Optional<RunRunnable> runnable = fsm.goToState(State.DELETING, null);
    //                 // Dispatch Runnable event to specific event listener es (serve,job,deploy...)
    //                 runnable.ifPresent(eventPublisher::publishEvent);

    //                 // Update run state to DELETING
    //                 RunBaseStatus runBaseStatus = RunBaseStatus.with(run.getStatus());
    //                 runBaseStatus.setState(State.DELETING.toString());

    //                 // Iterate over all processor and store all RunBaseStatus as optional
    //                 List<RunBaseStatus> processorsStatus = processorRegistry
    //                     .getProcessors("onDeleting")
    //                     .stream()
    //                     .map(processor -> processor.process(run, runnable.orElse(null), runBaseStatus))
    //                     .filter(Objects::nonNull)
    //                     .collect(Collectors.toList());

    //                 Map<String, Serializable> runStatusMap = processorsStatus
    //                     .stream()
    //                     .map(RunBaseStatus::toMap)
    //                     .reduce(new HashMap<>(), MapUtils::mergeMultipleMaps);

    //                 run.setStatus(MapUtils.mergeMultipleMaps(run.getStatus(), runBaseStatus.toMap(), runStatusMap));

    //                 //update
    //                 Run updated = entityService.update(run.getId(), run);

    //                 //if runnable we are deleting in async, otherwise move to delete
    //                 if (runnable.isEmpty()) {
    //                     //dispatch event
    //                     onDeleted(updated, null);
    //                 }

    //                 return updated;
    //             } catch (InvalidTransitionException e) {
    //                 // log error
    //                 log.debug("Invalid transaction from state {}  to state {}", e.getFromState(), e.getToState());
    //                 throw e;
    //             }
    //         } catch (StoreException e) {
    //             log.error("store error: {}", e.getMessage());
    //             throw new SystemException(e.getMessage());
    //         } finally {
    //             getLock(id).unlock();
    //         }
    //     } catch (InterruptedException e) {
    //         throw new SystemException("unable to acquire lock: " + e.getMessage());
    //     }
    // }

    // // @Async
    // // @EventListener
    // // public void onChangedEvent(RunnableChangedEvent<RunRunnable> event) throws StoreException {
    // //     if (event.getRunMonitorObject() == null) {
    // //         return;
    // //     }

    // //     log.debug("onChanged run with id {}: {}", event.getId(), event.getRunMonitorObject().getStateId());
    // //     if (log.isTraceEnabled()) {
    // //         log.trace("event: {}", event);
    // //     }

    // //     try {
    // //         // Retrieve the RunMonitorObject from the event
    // //         RunnableMonitorObject runnableMonitorObject = event.getRunMonitorObject();
    // //         String id = event.getId();

    // //         //acquire write lock
    // //         getLock(id).tryLock(timeout, TimeUnit.SECONDS);

    // //         try {
    // //             // Use service to retrieve the run and check if state is changed
    // //             Run run = entityService.find(id);
    // //             if (run == null) {
    // //                 log.error("Run with id {} not found", runnableMonitorObject.getRunId());
    // //                 return;
    // //             }

    // //             if (
    // //                 //either signal an update or track progress (running state)
    // //                 !Objects.equals(
    // //                     StatusFieldAccessor.with(run.getStatus()).getState(),
    // //                     runnableMonitorObject.getStateId()
    // //                 ) ||
    // //                 State.RUNNING == State.valueOf(runnableMonitorObject.getStateId())
    // //             ) {
    // //                 switch (State.valueOf(runnableMonitorObject.getStateId())) {
    // //                     case COMPLETED:
    // //                         onCompleted(run, event);
    // //                         break;
    // //                     case ERROR:
    // //                         onError(run, event);
    // //                         break;
    // //                     case RUNNING:
    // //                         onRunning(run, event);
    // //                         break;
    // //                     case STOPPED:
    // //                         onStopped(run, event);
    // //                         break;
    // //                     case DELETED:
    // //                         onDeleted(run, event);
    // //                         break;
    // //                     default:
    // //                         log.debug(
    // //                             "State {} for run id {} not managed",
    // //                             runnableMonitorObject.getStateId(),
    // //                             runnableMonitorObject.getRunId()
    // //                         );
    // //                         break;
    // //                 }
    // //             } else {
    // //                 log.debug(
    // //                     "State {} for run id {} not changed",
    // //                     runnableMonitorObject.getStateId(),
    // //                     runnableMonitorObject.getRunId()
    // //                 );
    // //             }
    // //         } catch (StoreException e) {
    // //             log.error("store error for {}:{}", runnableMonitorObject.getRunId(), e.getMessage());
    // //         } finally {
    // //             getLock(id).unlock();
    // //         }
    // //     } catch (InterruptedException e) {
    // //         throw new SystemException("unable to acquire lock: " + e.getMessage());
    // //     }
    // // }

    // /*
    //  * Event callback Methods
    //  */
    // public Run onRunning(Run run, RunnableChangedEvent<RunRunnable> event)
    //     throws NoSuchEntityException, StoreException {
    //     log.debug("onRunning run with id {}", run.getId());
    //     if (log.isTraceEnabled()) {
    //         log.trace("run: {}", run);
    //     }

    //     try {
    //         String id = run.getId();

    //         //acquire write lock
    //         getLock(id).tryLock(timeout, TimeUnit.SECONDS);

    //         try {
    //             // Try to move forward state machine based on current state
    //             Fsm<State, RunEvent, Map<String, Serializable>> fsm = createFsm(run);

    //             // Read spec and retrieve executables
    //             RunSpecAccessor runSpecAccessor = RunSpecAccessor.with(run.getSpec());

    //             //resolve runtime either from runSpec or from taskSpec
    //             String kind = runSpecAccessor.getRuntime() != null
    //                 ? runSpecAccessor.getRuntime()
    //                 : TaskSpecAccessor.with(taskEntityService.get(runSpecAccessor.getTaskId()).getSpec()).getRuntime();

    //             // Retrieve Runtime
    //             Runtime<
    //                 ? extends ExecutableBaseSpec,
    //                 ? extends RunBaseSpec,
    //                 ? extends RunBaseStatus,
    //                 ? extends RunRunnable
    //             > runtime = runtimeFactory.getRuntime(kind);

    //             // Define logic for state READY
    //             fsm
    //                 .getState(State.READY)
    //                 .getTransition(RunEvent.EXECUTE)
    //                 .setInternalLogic((context, input, fsmInstance) -> {
    //                     log.debug(
    //                         "Executing internal logic for state RUNNING, " + "event :{}, input: {}",
    //                         RunEvent.EXECUTE,
    //                         input
    //                     );
    //                     if (log.isTraceEnabled()) {
    //                         log.trace("Executing internal logic for state RUNNING, " + "context: {}", context);
    //                     }

    //                     RunRunnable runnable = event != null ? event.getRunnable() : null;
    //                     RunBaseStatus runStatus = runtime.onRunning(run, runnable);
    //                     return Optional.ofNullable(runStatus);
    //                 });
    //             fsm
    //                 .getState(State.RUNNING)
    //                 .getTransition(RunEvent.LOOP)
    //                 .setInternalLogic((context, input, fsmInstance) -> {
    //                     log.debug(
    //                         "Executing internal logic for state RUNNING, " + "event :{},  input: {}",
    //                         RunEvent.LOOP,
    //                         input
    //                     );
    //                     if (log.isTraceEnabled()) {
    //                         log.trace("Executing internal logic for state RUNNING, " + "context: {}", context);
    //                     }
    //                     RunRunnable runnable = event != null ? event.getRunnable() : null;
    //                     RunBaseStatus runStatus = runtime.onRunning(run, runnable);
    //                     return Optional.ofNullable(runStatus);
    //                 });
    //             fsm
    //                 .getState(State.RESUME)
    //                 .getTransition(RunEvent.EXECUTE)
    //                 .setInternalLogic((context, input, fsmInstance) -> {
    //                     log.debug(
    //                         "Executing internal logic for state RESUME, " + "event :{},  input: {}",
    //                         RunEvent.EXECUTE,
    //                         input
    //                     );
    //                     if (log.isTraceEnabled()) {
    //                         log.trace("Executing internal logic for state RESUME, " + "context: {}", context);
    //                     }
    //                     RunRunnable runnable = event != null ? event.getRunnable() : null;
    //                     RunBaseStatus runStatus = runtime.onRunning(run, runnable);
    //                     return Optional.ofNullable(runStatus);
    //                 });

    //             //TODO call registry processor to retrieve all processor for onRunning and call process()
    //             Optional<RunBaseStatus> runStatus = fsm.goToState(State.RUNNING, null);

    //             // Update run status
    //             RunBaseStatus runBaseStatus = runStatus
    //                 .map(r -> {
    //                     r.setState(State.RUNNING.toString());
    //                     return r;
    //                 })
    //                 .orElseGet(() -> new RunBaseStatus(State.RUNNING.toString()));

    //             RunRunnable runRunnable = event != null ? event.getRunnable() : null;

    //             // Iterate over all processor and store all RunBaseStatus as optional
    //             List<RunBaseStatus> processorsStatus = processorRegistry
    //                 .getProcessors("onRunning")
    //                 .stream()
    //                 .map(processor -> processor.process(run, runRunnable, runBaseStatus))
    //                 .filter(Objects::nonNull)
    //                 .collect(Collectors.toList());

    //             Map<String, Serializable> runStatusMap = processorsStatus
    //                 .stream()
    //                 .map(RunBaseStatus::toMap)
    //                 .reduce(new HashMap<>(), MapUtils::mergeMultipleMaps);

    //             run.setStatus(MapUtils.mergeMultipleMaps(run.getStatus(), runBaseStatus.toMap(), runStatusMap));

    //             return entityService.update(run.getId(), run);
    //         } catch (InvalidTransitionException e) {
    //             log.debug("Invalid transaction from state {}  to state {}", e.getFromState(), e.getToState());
    //             return run;
    //         } catch (StoreException e) {
    //             log.error("store error: {}", e.getMessage());
    //             throw new SystemException(e.getMessage());
    //         } finally {
    //             getLock(id).unlock();
    //         }
    //     } catch (InterruptedException e) {
    //         throw new SystemException("unable to acquire lock: " + e.getMessage());
    //     }
    // }

    // /**
    //  * A callback method to handle complete, update run status and execute internal logic for the COMPLETE state.
    //  *
    //  * @param event the RunChangedEvent triggering the error
    //  * @throws StoreException
    //  * @throws NoSuchEntityException if the entity being accessed does not exist
    //  */
    // public void onCompleted(Run run, RunnableChangedEvent<RunRunnable> event)
    //     throws NoSuchEntityException, StoreException {
    //     log.debug("onCompleted run with id {}", run.getId());
    //     if (log.isTraceEnabled()) {
    //         log.trace("run: {}", run);
    //     }

    //     try {
    //         String id = run.getId();

    //         //acquire write lock
    //         getLock(id).tryLock(timeout, TimeUnit.SECONDS);

    //         try {
    //             // Try to move forward state machine based on current state
    //             Fsm<State, RunEvent, Map<String, Serializable>> fsm = createFsm(run);

    //             // Read spec and retrieve executables
    //             RunSpecAccessor runSpecAccessor = RunSpecAccessor.with(run.getSpec());

    //             //resolve runtime either from runSpec or from taskSpec
    //             String kind = runSpecAccessor.getRuntime() != null
    //                 ? runSpecAccessor.getRuntime()
    //                 : TaskSpecAccessor.with(taskEntityService.get(runSpecAccessor.getTaskId()).getSpec()).getRuntime();

    //             // Retrieve Runtime
    //             Runtime<
    //                 ? extends ExecutableBaseSpec,
    //                 ? extends RunBaseSpec,
    //                 ? extends RunBaseStatus,
    //                 ? extends RunRunnable
    //             > runtime = runtimeFactory.getRuntime(kind);

    //             // Define logic for state RUNNING
    //             fsm
    //                 .getState(State.RUNNING)
    //                 .getTransition(RunEvent.COMPLETE)
    //                 .setInternalLogic((context, input, fsmInstance) -> {
    //                     log.debug(
    //                         "Executing internal logic for state RUNNING, " + "event :{}, input: {}",
    //                         RunEvent.COMPLETE,
    //                         input
    //                     );
    //                     if (log.isTraceEnabled()) {
    //                         log.trace("Executing internal logic for state RUNNING, " + "context: {}", context);
    //                     }
    //                     RunRunnable runnable = event != null ? event.getRunnable() : null;
    //                     RunBaseStatus runStatus = runtime.onComplete(run, runnable);
    //                     return Optional.ofNullable(runStatus);
    //                 });

    //             fsm
    //                 .getState(State.COMPLETED)
    //                 .getTransition(RunEvent.DELETING)
    //                 .setInternalLogic((context, input, fsmInstance) -> {
    //                     log.debug(
    //                         "Executing internal logic for state COMPLETED, " + "event :{}, input: {}",
    //                         RunEvent.DELETING,
    //                         input
    //                     );
    //                     if (log.isTraceEnabled()) {
    //                         log.trace("Executing internal logic for state COMPLETED, " + "context: {}", context);
    //                     }
    //                     RunRunnable runnable = runtime.delete(run);
    //                     return Optional.ofNullable(runnable);
    //                 });

    //             try {
    //                 Optional<RunBaseStatus> runStatus = fsm.goToState(State.COMPLETED, null);

    //                 RunBaseStatus runBaseStatus = runStatus
    //                     .map(r -> {
    //                         r.setState(State.COMPLETED.toString());
    //                         return r;
    //                     })
    //                     .orElseGet(() -> new RunBaseStatus(State.COMPLETED.toString()));

    //                 RunRunnable runRunnable = event != null ? event.getRunnable() : null;

    //                 // Iterate over all processor and store all RunBaseStatus as optional
    //                 List<RunBaseStatus> processorsStatus = processorRegistry
    //                     .getProcessors("onCompleted")
    //                     .stream()
    //                     .map(processor -> processor.process(run, runRunnable, runBaseStatus))
    //                     .filter(Objects::nonNull)
    //                     .collect(Collectors.toList());

    //                 Map<String, Serializable> runStatusMap = processorsStatus
    //                     .stream()
    //                     .map(RunBaseStatus::toMap)
    //                     .reduce(new HashMap<>(), MapUtils::mergeMultipleMaps);

    //                 run.setStatus(MapUtils.mergeMultipleMaps(run.getStatus(), runBaseStatus.toMap(), runStatusMap));

    //                 //update
    //                 Run updated = entityService.update(run.getId(), run);

    //                 //dispatch delete, we are in a final state
    //                 Optional<RunRunnable> runnable = fsm.goToState(State.DELETING, null);
    //                 runnable.ifPresent(runEvent -> {
    //                     //dispatch event
    //                     eventPublisher.publishEvent(runEvent);
    //                 });
    //                 if (runnable.isEmpty()) {
    //                     //directly dispatch callback event
    //                     onDeleted(updated, null);
    //                 }
    //             } catch (InvalidTransitionException e) {
    //                 log.debug("Invalid transaction from state {}  to state {}", e.getFromState(), e.getToState());
    //             }
    //         } catch (StoreException e) {
    //             log.error("store error: {}", e.getMessage());
    //             throw new SystemException(e.getMessage());
    //         } finally {
    //             getLock(id).unlock();
    //         }
    //     } catch (InterruptedException e) {
    //         throw new SystemException("unable to acquire lock: " + e.getMessage());
    //     }
    // }

    // /**
    //  * A callback method to handle stop in the run, update run status and execute internal logic for the STOP state.
    //  *
    //  * @param event the RunChangedEvent triggering the stop
    //  * @throws StoreException
    //  * @throws NoSuchEntityException if the entity being accessed does not exist
    //  */
    // public void onStopped(Run run, RunnableChangedEvent<RunRunnable> event)
    //     throws NoSuchEntityException, StoreException {
    //     log.debug("onStopped run with id {}", run.getId());
    //     if (log.isTraceEnabled()) {
    //         log.trace("run: {}", run);
    //     }

    //     try {
    //         String id = run.getId();

    //         //acquire write lock
    //         getLock(id).tryLock(timeout, TimeUnit.SECONDS);

    //         try {
    //             // Try to move forward state machine based on current state
    //             Fsm<State, RunEvent, Map<String, Serializable>> fsm = createFsm(run);
    //             // Read spec and retrieve executables
    //             RunSpecAccessor runSpecAccessor = RunSpecAccessor.with(run.getSpec());

    //             //resolve runtime either from runSpec or from taskSpec
    //             String kind = runSpecAccessor.getRuntime() != null
    //                 ? runSpecAccessor.getRuntime()
    //                 : TaskSpecAccessor.with(taskEntityService.get(runSpecAccessor.getTaskId()).getSpec()).getRuntime();

    //             // Retrieve Runtime
    //             Runtime<
    //                 ? extends ExecutableBaseSpec,
    //                 ? extends RunBaseSpec,
    //                 ? extends RunBaseStatus,
    //                 ? extends RunRunnable
    //             > runtime = runtimeFactory.getRuntime(kind);

    //             // Define logic for state STOP
    //             fsm
    //                 .getState(State.STOP)
    //                 .getTransition(RunEvent.STOP)
    //                 .setInternalLogic((context, input, fsmInstance) -> {
    //                     log.debug(
    //                         "Executing internal logic for state STOP, " + "event :{}, input: {}",
    //                         RunEvent.STOP,
    //                         input
    //                     );
    //                     if (log.isTraceEnabled()) {
    //                         log.trace("Executing internal logic for state STOP, " + "context: {}", context);
    //                     }
    //                     RunRunnable runnable = event != null ? event.getRunnable() : null;
    //                     RunBaseStatus runStatus = runtime.onStopped(run, runnable);
    //                     return Optional.ofNullable(runStatus);
    //                 });

    //             try {
    //                 Optional<RunBaseStatus> runStatus = fsm.goToState(State.STOPPED, null);

    //                 // Update run status
    //                 RunBaseStatus runBaseStatus = runStatus
    //                     .map(r -> {
    //                         r.setState(State.STOPPED.toString());
    //                         return r;
    //                     })
    //                     .orElseGet(() -> new RunBaseStatus(State.STOPPED.toString()));

    //                 RunRunnable runRunnable = event != null ? event.getRunnable() : null;

    //                 // Iterate over all processor and store all RunBaseStatus as optional
    //                 List<RunBaseStatus> processorsStatus = processorRegistry
    //                     .getProcessors("onStopped")
    //                     .stream()
    //                     .map(processor -> processor.process(run, runRunnable, runBaseStatus))
    //                     .filter(Objects::nonNull)
    //                     .collect(Collectors.toList());

    //                 Map<String, Serializable> runStatusMap = processorsStatus
    //                     .stream()
    //                     .map(RunBaseStatus::toMap)
    //                     .reduce(new HashMap<>(), MapUtils::mergeMultipleMaps);

    //                 run.setStatus(MapUtils.mergeMultipleMaps(run.getStatus(), runBaseStatus.toMap(), runStatusMap));

    //                 entityService.update(run.getId(), run);
    //             } catch (InvalidTransitionException e) {
    //                 log.debug("Invalid transaction from state {}  to state {}", e.getFromState(), e.getToState());
    //             }
    //         } catch (StoreException e) {
    //             log.error("store error: {}", e.getMessage());
    //             throw new SystemException(e.getMessage());
    //         } finally {
    //             getLock(id).unlock();
    //         }
    //     } catch (InterruptedException e) {
    //         throw new SystemException("unable to acquire lock: " + e.getMessage());
    //     }
    // }

    // /**
    //  * A callback method to handle errors in the run, update run status and execute internal logic for the ERROR state.
    //  *
    //  * @param event the RunChangedEvent triggering the error
    //  * @throws StoreException
    //  * @throws NoSuchEntityException if the entity being accessed does not exist
    //  */
    // public void onError(Run run, RunnableChangedEvent<RunRunnable> event) throws NoSuchEntityException, StoreException {
    //     log.debug("onError run with id {}", run.getId());
    //     if (log.isTraceEnabled()) {
    //         log.trace("run: {}", run);
    //     }

    //     try {
    //         String id = run.getId();

    //         //acquire write lock
    //         getLock(id).tryLock(timeout, TimeUnit.SECONDS);

    //         try {
    //             // Try to move forward state machine based on current state
    //             Fsm<State, RunEvent, Map<String, Serializable>> fsm = createFsm(run);

    //             // Read spec and retrieve executables
    //             RunSpecAccessor runSpecAccessor = RunSpecAccessor.with(run.getSpec());

    //             //resolve runtime either from runSpec or from taskSpec
    //             String kind = runSpecAccessor.getRuntime() != null
    //                 ? runSpecAccessor.getRuntime()
    //                 : TaskSpecAccessor.with(taskEntityService.get(runSpecAccessor.getTaskId()).getSpec()).getRuntime();

    //             Runtime<
    //                 ? extends ExecutableBaseSpec,
    //                 ? extends RunBaseSpec,
    //                 ? extends RunBaseStatus,
    //                 ? extends RunRunnable
    //             > runtime = runtimeFactory.getRuntime(kind);

    //             fsm
    //                 .getState(State.BUILT)
    //                 .getTransition(RunEvent.ERROR)
    //                 .setInternalLogic((context, input, fsmInstance) -> {
    //                     log.debug(
    //                         "Executing internal logic for state BUILT, " + "event :{}, input: {}",
    //                         RunEvent.ERROR,
    //                         input
    //                     );
    //                     if (log.isTraceEnabled()) {
    //                         log.trace("Executing internal logic for state BUILT, " + "context: {}", context);
    //                     }
    //                     RunRunnable runnable = event != null ? event.getRunnable() : null;
    //                     RunBaseStatus runStatus = runtime.onError(run, runnable);
    //                     return Optional.ofNullable(runStatus);
    //                 });

    //             fsm
    //                 .getState(State.READY)
    //                 .getTransition(RunEvent.ERROR)
    //                 .setInternalLogic((context, input, fsmInstance) -> {
    //                     log.debug(
    //                         "Executing internal logic for state READY, " + "event :{}, input: {}",
    //                         RunEvent.ERROR,
    //                         input
    //                     );
    //                     if (log.isTraceEnabled()) {
    //                         log.trace("Executing internal logic for state READY, " + "context: {}", context);
    //                     }
    //                     RunRunnable runnable = event != null ? event.getRunnable() : null;
    //                     RunBaseStatus runStatus = runtime.onError(run, runnable);
    //                     return Optional.ofNullable(runStatus);
    //                 });

    //             fsm
    //                 .getState(State.RUNNING)
    //                 .getTransition(RunEvent.ERROR)
    //                 .setInternalLogic((context, input, fsmInstance) -> {
    //                     log.debug(
    //                         "Executing internal logic for state RUNNING, " + "event :{}, input: {}",
    //                         RunEvent.ERROR,
    //                         input
    //                     );
    //                     if (log.isTraceEnabled()) {
    //                         log.trace("Executing internal logic for state RUNNING, " + "context: {}", context);
    //                     }
    //                     RunRunnable runnable = event != null ? event.getRunnable() : null;
    //                     RunBaseStatus runStatus = runtime.onError(run, runnable);
    //                     return Optional.ofNullable(runStatus);
    //                 });

    //             fsm
    //                 .getState(State.STOP)
    //                 .getTransition(RunEvent.ERROR)
    //                 .setInternalLogic((context, input, fsmInstance) -> {
    //                     log.debug(
    //                         "Executing internal logic for state STOP, " + "event :{}, input: {}",
    //                         RunEvent.ERROR,
    //                         input
    //                     );
    //                     if (log.isTraceEnabled()) {
    //                         log.trace("Executing internal logic for state STOP, " + "context: {}", context);
    //                     }
    //                     RunRunnable runnable = event != null ? event.getRunnable() : null;
    //                     RunBaseStatus runStatus = runtime.onError(run, runnable);
    //                     return Optional.ofNullable(runStatus);
    //                 });

    //             fsm
    //                 .getState(State.ERROR)
    //                 .getTransition(RunEvent.DELETING)
    //                 .setInternalLogic((context, input, fsmInstance) -> {
    //                     log.debug(
    //                         "Executing internal logic for state ERROR, " + "event :{}, input: {}",
    //                         RunEvent.DELETING,
    //                         input
    //                     );
    //                     if (log.isTraceEnabled()) {
    //                         log.trace("Executing internal logic for state ERROR, " + "context: {}", context);
    //                     }
    //                     RunRunnable runnable = runtime.delete(run);
    //                     return Optional.ofNullable(runnable);
    //                 });

    //             try {
    //                 Optional<RunBaseStatus> runStatus = fsm.goToState(State.ERROR, null);

    //                 // Update run status
    //                 RunBaseStatus runBaseStatus = runStatus
    //                     .map(r -> {
    //                         r.setState(State.ERROR.toString());
    //                         return r;
    //                     })
    //                     .orElseGet(() -> new RunBaseStatus(State.ERROR.toString()));

    //                 RunRunnable runRunnable = event != null ? event.getRunnable() : null;

    //                 // Iterate over all processor and store all RunBaseStatus as optional
    //                 List<RunBaseStatus> processorsStatus = processorRegistry
    //                     .getProcessors("onError")
    //                     .stream()
    //                     .map(processor -> processor.process(run, runRunnable, runBaseStatus))
    //                     .filter(Objects::nonNull)
    //                     .collect(Collectors.toList());

    //                 Map<String, Serializable> runStatusMap = processorsStatus
    //                     .stream()
    //                     .map(RunBaseStatus::toMap)
    //                     .reduce(new HashMap<>(), MapUtils::mergeMultipleMaps);

    //                 run.setStatus(MapUtils.mergeMultipleMaps(run.getStatus(), runBaseStatus.toMap(), runStatusMap));

    //                 //update
    //                 Run updated = entityService.update(run.getId(), run);

    //                 //dispatch delete, we are in a final state
    //                 Optional<RunRunnable> runnable = fsm.goToState(State.DELETING, null);
    //                 runnable.ifPresent(runEvent -> {
    //                     //dispatch event
    //                     eventPublisher.publishEvent(runEvent);
    //                 });
    //                 if (runnable.isEmpty()) {
    //                     //directly dispatch callback event
    //                     onDeleted(updated, null);
    //                 }
    //             } catch (InvalidTransitionException e) {
    //                 log.debug("Invalid transaction from state {} to state {}", e.getFromState(), e.getToState());
    //             }
    //         } catch (StoreException e) {
    //             log.error("store error: {}", e.getMessage());
    //             throw new SystemException(e.getMessage());
    //         } finally {
    //             getLock(id).unlock();
    //         }
    //     } catch (InterruptedException e) {
    //         throw new SystemException("unable to acquire lock: " + e.getMessage());
    //     }
    // }

    // /**
    //  * A method to handle delete in the run, update run status and execute internal logic for the STOP state.
    //  *
    //  * @param event the RunChangedEvent triggering the delete
    //  * @throws StoreException
    //  * @throws NoSuchEntityException if the entity being accessed does not exist
    //  */
    // public void onDeleted(Run run, RunnableChangedEvent<RunRunnable> event)
    //     throws NoSuchEntityException, StoreException {
    //     log.debug("onDeleted run with id {}", run.getId());
    //     if (log.isTraceEnabled()) {
    //         log.trace("run: {}", run);
    //     }

    //     try {
    //         String id = run.getId();

    //         //acquire write lock
    //         getLock(id).tryLock(timeout, TimeUnit.SECONDS);

    //         try {
    //             // Try to move forward state machine based on current state
    //             Fsm<State, RunEvent, Map<String, Serializable>> fsm = createFsm(run);

    //             //check if we are in a deleting or finalize flow
    //             String curState = StatusFieldAccessor.with(run.getStatus()).getState();
    //             boolean toDelete = State.DELETING.name().equals(curState);

    //             // Read spec and retrieve executables
    //             RunSpecAccessor runSpecAccessor = RunSpecAccessor.with(run.getSpec());

    //             //resolve runtime either from runSpec or from taskSpec
    //             String kind = runSpecAccessor.getRuntime() != null
    //                 ? runSpecAccessor.getRuntime()
    //                 : TaskSpecAccessor.with(taskEntityService.get(runSpecAccessor.getTaskId()).getSpec()).getRuntime();

    //             Runtime<
    //                 ? extends ExecutableBaseSpec,
    //                 ? extends RunBaseSpec,
    //                 ? extends RunBaseStatus,
    //                 ? extends RunRunnable
    //             > runtime = runtimeFactory.getRuntime(kind);

    //             // Define logic for state DELETING
    //             fsm
    //                 .getState(State.DELETING)
    //                 .getTransition(RunEvent.DELETING)
    //                 .setInternalLogic((context, input, fsmInstance) -> {
    //                     log.debug(
    //                         "Executing internal logic for state DELETING, " + "event :{}, input: {}",
    //                         RunEvent.DELETING,
    //                         input
    //                     );
    //                     if (log.isTraceEnabled()) {
    //                         log.trace("Executing internal logic for state DELETING, " + "context: {}", context);
    //                     }
    //                     RunRunnable runnable = event != null ? event.getRunnable() : null;
    //                     RunBaseStatus runStatus = runtime.onDeleted(run, runnable);
    //                     return Optional.ofNullable(runStatus);
    //                 });

    //             // Define logic for state ERROR
    //             fsm
    //                 .getState(State.ERROR)
    //                 .getTransition(RunEvent.DELETING)
    //                 .setInternalLogic((context, input, fsmInstance) -> {
    //                     log.debug(
    //                         "Executing internal logic for state ERROR, " + "event :{}, input: {}",
    //                         RunEvent.DELETING,
    //                         input
    //                     );
    //                     if (log.isTraceEnabled()) {
    //                         log.trace("Executing internal logic for state ERROR, " + "context: {}", context);
    //                     }
    //                     RunRunnable runnable = event != null ? event.getRunnable() : null;
    //                     RunBaseStatus runStatus = runtime.onDeleted(run, runnable);
    //                     return Optional.ofNullable(runStatus);
    //                 });

    //             // Define logic for state COMPLETED
    //             fsm
    //                 .getState(State.COMPLETED)
    //                 .getTransition(RunEvent.DELETING)
    //                 .setInternalLogic((context, input, fsmInstance) -> {
    //                     log.debug(
    //                         "Executing internal logic for state COMPLETED, " + "event :{}, input: {}",
    //                         RunEvent.DELETING,
    //                         input
    //                     );
    //                     if (log.isTraceEnabled()) {
    //                         log.trace("Executing internal logic for state COMPLETED, " + "context: {}", context);
    //                     }
    //                     RunRunnable runnable = event != null ? event.getRunnable() : null;
    //                     RunBaseStatus runStatus = runtime.onDeleted(run, runnable);
    //                     return Optional.ofNullable(runStatus);
    //                 });

    //             try {
    //                 Optional<RunBaseStatus> runStatus = fsm.goToState(State.DELETED, null);

    //                 // Update run status
    //                 RunBaseStatus runBaseStatus = runStatus
    //                     .map(r -> {
    //                         if (toDelete) {
    //                             //explicit delete request leads to deleted status
    //                             r.setState(State.DELETED.toString());
    //                         } else {
    //                             //keep state as-is
    //                             r.setState(curState);
    //                         }

    //                         return r;
    //                     })
    //                     .orElseGet(() -> {
    //                         RunBaseStatus r = new RunBaseStatus();
    //                         if (toDelete) {
    //                             //explicit delete request leads to deleted status
    //                             r.setState(State.DELETED.toString());
    //                         } else {
    //                             //keep state as-is
    //                             r.setState(curState);
    //                         }

    //                         return r;
    //                     });

    //                 RunRunnable runRunnable = event != null ? event.getRunnable() : null;

    //                 // Iterate over all processor and store all RunBaseStatus as optional
    //                 List<RunBaseStatus> processorsStatus = processorRegistry
    //                     .getProcessors("onDeleted")
    //                     .stream()
    //                     .map(processor -> processor.process(run, runRunnable, runBaseStatus))
    //                     .filter(Objects::nonNull)
    //                     .collect(Collectors.toList());

    //                 Map<String, Serializable> runStatusMap = processorsStatus
    //                     .stream()
    //                     .map(RunBaseStatus::toMap)
    //                     .reduce(new HashMap<>(), MapUtils::mergeMultipleMaps);

    //                 run.setStatus(MapUtils.mergeMultipleMaps(run.getStatus(), runBaseStatus.toMap(), runStatusMap));

    //                 entityService.update(run.getId(), run);

    //                 if (toDelete) {
    //                     //delete run via service to handle cascade
    //                     runService.deleteRun(run.getId(), Boolean.TRUE);
    //                 }
    //             } catch (InvalidTransitionException e) {
    //                 log.debug("Invalid transaction from state {}  to state {}", e.getFromState(), e.getToState());
    //             }
    //         } catch (StoreException e) {
    //             log.error("store error: {}", e.getMessage());
    //             throw new SystemException(e.getMessage());
    //         } finally {
    //             getLock(id).unlock();
    //         }
    //     } catch (InterruptedException e) {
    //         throw new SystemException("unable to acquire lock: " + e.getMessage());
    //     }
    // }

    // /**
    //  * Creates and returns a finite state machine (FSM) with the specified initial state and context and run.
    //  *
    //  * @param run the run object used to retrieve the entity and initialize the state machine context
    //  * @return the FSM created and initialized for the given run
    //  */
    // private Fsm<State, RunEvent, Map<String, Serializable>> createFsm(Run run) {
    //     // Retrieve entity from run dto

    //     // Create state machine context
    //     Map<String, Serializable> ctx = new HashMap<>();
    //     ctx.put("run", run);

    //     // Initialize state machine
    //     Fsm<State, RunEvent, Map<String, Serializable>> fsm = runStateMachine.build(
    //         State.valueOf(StatusFieldAccessor.with(run.getStatus()).getState()),
    //         ctx
    //     );

    //     // On state change delegate state machine to update the run
    //     fsm.setStateChangeListener((state, context) -> {
    //         if (log.isTraceEnabled()) {
    //             log.trace("State Change Listener: {}, context: {}", state, context);
    //         }
    //     });
    //     return fsm;
    // }
    // // /**
    // //  * Retrieve an executable based on the given run.
    // //  *
    // //  * @param run the run to retrieve the executable for
    // //  * @return the retrieved executable
    // //  * @throws StoreException
    // //  */
    // // private Executable retrieveExecutable(Run run) throws NoSuchEntityException, StoreException {
    // //     // GET state machine, init state machine with status
    // //     RunBaseSpec runBaseSpec = new RunBaseSpec();
    // //     runBaseSpec.configure(run.getSpec());
    // //     RunSpecAccessor runSpecAccessor = RunUtils.parseTask(runBaseSpec.getTask());

    // //     // Retrieve Executable
    // //     String executableId = runSpecAccessor.getVersion();
    // //     return executableEntityServiceProvider
    // //         .getEntityServiceByRuntime(runSpecAccessor.getRuntime())
    // //         .get(executableId);
    // // }

    // // private Specification<TaskEntity> createFunctionSpecification(String executable) {
    // //     return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("function"), executable);
    // // }

    // // private Specification<TaskEntity> createTaskKindSpecification(String kind) {
    // //     return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("kind"), kind);
    // // }
}
