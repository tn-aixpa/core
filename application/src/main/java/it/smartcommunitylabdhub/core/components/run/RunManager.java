package it.smartcommunitylabdhub.core.components.run;

import it.smartcommunitylabdhub.commons.accessors.spec.RunSpecAccessor;
import it.smartcommunitylabdhub.commons.events.RunChangedEvent;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.infrastructure.Runnable;
import it.smartcommunitylabdhub.commons.infrastructure.Runtime;
import it.smartcommunitylabdhub.commons.models.entities.function.Function;
import it.smartcommunitylabdhub.commons.models.entities.function.FunctionBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseStatus;
import it.smartcommunitylabdhub.commons.models.entities.task.Task;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.models.utils.RunUtils;
import it.smartcommunitylabdhub.commons.models.utils.TaskUtils;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.runtimes.RuntimeFactory;
import it.smartcommunitylabdhub.core.models.builders.run.RunEntityBuilder;
import it.smartcommunitylabdhub.core.models.entities.function.FunctionEntity;
import it.smartcommunitylabdhub.core.models.entities.log.LogEntity;
import it.smartcommunitylabdhub.core.models.entities.run.RunEntity;
import it.smartcommunitylabdhub.core.models.entities.task.TaskEntity;
import it.smartcommunitylabdhub.core.models.queries.specifications.CommonSpecification;
import it.smartcommunitylabdhub.core.repositories.LogRepository;
import it.smartcommunitylabdhub.core.repositories.RunRepository;
import it.smartcommunitylabdhub.core.services.EntityService;
import it.smartcommunitylabdhub.fsm.Fsm;
import it.smartcommunitylabdhub.fsm.enums.RunEvent;
import it.smartcommunitylabdhub.fsm.exceptions.InvalidTransactionException;
import it.smartcommunitylabdhub.fsm.types.RunStateMachineFactory;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RunManager {

    private final RunStateMachineFactory runStateMachine;

    private final RunRepository runRepository;

    private final LogRepository logRepository;

    private final EntityService<Run, RunEntity> entityService;


    private final EntityService<Task, TaskEntity> taskEntityService;

    private final EntityService<Function, FunctionEntity> functionEntityService;

    private final RunEntityBuilder runEntityBuilder;

    private final RuntimeFactory runtimeFactory;

    private final ApplicationEventPublisher eventPublisher;


    public RunManager(
            RunStateMachineFactory runStateMachine,
            RunRepository runRepository,
            LogRepository logRepository,
            EntityService<Run, RunEntity> entityService,
            EntityService<Task, TaskEntity> taskEntityService,
            EntityService<Function, FunctionEntity> functionEntityService,
            RunEntityBuilder runEntityBuilder,
            RuntimeFactory runtimeFactory, ApplicationEventPublisher eventPublisher
    ) {
        this.runStateMachine = runStateMachine;
        this.runRepository = runRepository;
        this.logRepository = logRepository;
        this.entityService = entityService;
        this.taskEntityService = taskEntityService;
        this.functionEntityService = functionEntityService;
        this.runEntityBuilder = runEntityBuilder;
        this.runtimeFactory = runtimeFactory;
        this.eventPublisher = eventPublisher;
    }


    public Run build(@NotNull Run run) throws NoSuchEntityException {

        // GET state machine, init state machine with status
        RunBaseSpec runBaseSpec = new RunBaseSpec();
        runBaseSpec.configure(run.getSpec());
        RunSpecAccessor runSpecAccessor = RunUtils.parseTask(runBaseSpec.getTask());

        // Retrieve Function
        String functionId = runSpecAccessor.getVersion();
        Function function = functionEntityService.get(functionId);

        // Retrieve Task
        Specification<TaskEntity> where = Specification.allOf(
                CommonSpecification.projectEquals(function.getProject()),
                createFunctionSpecification(TaskUtils.buildFunctionString(function)),
                createTaskKindSpecification(runSpecAccessor.getTask())
        );
        Task task = taskEntityService.searchAll(where).stream().findFirst().orElse(null);

        // Retrieve state machine
        Fsm<State, RunEvent, Map<String, Serializable>> fsm = createFsm(run);

        // Add Internal logic to be executed when state change from CREATED to READY
        fsm.getState(State.CREATED)
                .getTransaction(RunEvent.BUILD)
                .setInternalLogic((context, input, fsmInstance) -> {
                    if (!Optional.ofNullable(runBaseSpec.getLocalExecution()).orElse(Boolean.FALSE)) {
                        // Retrieve Runtime and build run
                        Runtime<? extends FunctionBaseSpec,
                                ? extends RunBaseSpec,
                                ? extends RunBaseStatus,
                                ? extends Runnable> runtime = runtimeFactory.getRuntime(function.getKind());

                        // Build RunSpec using Runtime now if wrong type is passed to a specific runtime
                        // an exception occur! for.
                        RunBaseSpec runSpecBuilt = runtime.build(function, task, run);

                        return Optional.of(runSpecBuilt);
                    }

                    return Optional.empty();
                });

        try {
            // Update run state to BUILT
            Optional<RunBaseSpec> runSpecBuilt = fsm.goToState(State.BUILT, null);
            runSpecBuilt.ifPresent(
                    spec -> {
                        // Update run spec
                        run.setSpec(spec.toMap());

                        // Update run state to BUILT
                        run.getStatus().put("state", State.BUILT.toString());

                        if (log.isTraceEnabled()) {
                            log.trace("Built run: {}", run);
                        }
                    });

            entityService.update(run.getId(), run);
            return run;
        } catch (InvalidTransactionException e) {
            // log error
            log.error("Invalid transaction from state {}  to state {}", State.CREATED, State.BUILT);
            throw new InvalidTransactionException(State.CREATED.toString(), State.BUILT.toString());
        }
    }


    public Run run(@NotNull Run run) throws NoSuchEntityException, InvalidTransactionException {

        // GET state machine, init state machine with status
        RunBaseSpec runBaseSpec = new RunBaseSpec();
        runBaseSpec.configure(run.getSpec());
        RunSpecAccessor runSpecAccessor = RunUtils.parseTask(runBaseSpec.getTask());

        // Retrieve Function
        String functionId = runSpecAccessor.getVersion();
        Function function = functionEntityService.get(functionId);

        // Retrieve state machine
        Fsm<State, RunEvent, Map<String, Serializable>> fsm = createFsm(run);

        fsm.getState(State.BUILT)
                .getTransaction(RunEvent.RUN)
                .setInternalLogic((context, input, stateMachine) -> {
                    if (!Optional.ofNullable(runBaseSpec.getLocalExecution()).orElse(Boolean.FALSE)) {
                        // Retrieve Runtime and build run
                        Runtime<? extends FunctionBaseSpec, ? extends RunBaseSpec, ? extends RunBaseStatus, ? extends Runnable> runtime =
                                runtimeFactory.getRuntime(function.getKind());
                        // Create Runnable
                        Runnable runnable = runtime.run(run);

                        return Optional.of(runnable);
                    } else {
                        return Optional.empty();
                    }
                });

        try {
            Optional<Runnable> runnable = fsm.goToState(State.READY, null);
            runnable.ifPresent(r -> {

                // Dispatch Runnable
                eventPublisher.publishEvent(r);

                run.getStatus().put("state", State.READY.toString());

            });

            entityService.update(run.getId(), run);

            return run;
        } catch (InvalidTransactionException e) {
            // log error
            log.error("Invalid transaction from state {}  to state {}", State.BUILT, State.READY);
            throw new InvalidTransactionException(State.BUILT.toString(), State.READY.toString());
        }
    }


    public Run stop(@NotNull Run run) {
        /// check the one above...

        return run;
    }

    @Async
    @EventListener
    public void onRunning(RunChangedEvent event) {
        //TODO need to do the onRunning method
//        // Retrieve the RunMonitorObject from the event
//        RunMonitorObject runMonitorObject = event.getRunMonitorObject();
//
//        // Find the related RunEntity
//        runRepository
//                .findById(runMonitorObject.getRunId())
//                .stream()
//                .filter(runEntity -> !runEntity.getState().name().equals(runMonitorObject.getStateId()))
//                .findAny()
//                .ifPresentOrElse(
//                        runEntity -> {
//                            // Try to move forward state machine based on current state
//                            createFsm(runEntity).goToState(State.valueOf(runMonitorObject.getStateId()), null);
//                            System.out.println("jello");
//                        },
//                        () -> {
//                            error(runMonitorObject.getRunId());
//                            log.error("Run with id {} not found", runMonitorObject.getRunId());
//                        }
//                );
    }


    @Async
    @EventListener
    public void onComplete(RunChangedEvent event) {

        // Get Run
        // Create state machine

        // Lambda
        // define internal logic of the state
        // execute state machine -> Runtime.onComplete
        // output from runtime.onComplete is the specific RunStatus
        // End lambda

        //Get Run status -> set to status complete and merge with RunStatus received from lambda that is the goToState.


    }

    @Async
    @EventListener
    public void onError(RunChangedEvent event) {

    }


    @Async
    @EventListener
    public void log(LogEntity logEntity) {
        logRepository.save(logEntity);
    }

    public void error(String id) {
    }

    private Fsm<State, RunEvent, Map<String, Serializable>> createFsm(Run run) {

        // Retrieve entity from run dto
        RunEntity runEntity = runEntityBuilder.build(run);

        // Create state machine context
        Map<String, Serializable> ctx = new HashMap<>();
        ctx.put("run", run);

        // Initialize state machine
        Fsm<State, RunEvent, Map<String, Serializable>> fsm = runStateMachine.builder(
                State.valueOf(runEntity.getState().toString()), ctx
        );

        // On state change delegate state machine to update the run
        fsm.setStateChangeListener((state, context) -> {
            log.info("State Change Listener: {}, context: {}", state, context);
        });
        return fsm;


        //        // DEFINING CUSTOM STATE MACHINE BEHAVIOUR
//        fsm
//                .getState(State.READY)
//                .setExitAction(context -> {
//                    log.info("EXITING FROM READY STATE");
//                });
//        fsm
//                .getState(State.READY)
//                .getTransaction(RunEvent.EXECUTE)
//                .setInternalLogic((context, input, fsmInstance) -> {
//                    log.info(
//                            "Executing internal logic for state READY, context: {}, input: {}",
//                            context, input
//                    );
//                    return Optional.of("Hello");
//                });
//        fsm
//                .getState(State.RUNNING)
//                .getTransaction(RunEvent.COMPLETE)
//                .setInternalLogic((context, input, fsmInstance) -> {
//                    log.info("Executing internal logic for state RUNNING, context: {}, input: {}",
//                            context, input);
//                    return Optional.of("Hello");
//                });
//        fsm.setEventListener(
//                RunEvent.ERROR,
//                (context, input) -> {
//                    // do something
//                    // notifiy log when error happend
//                    // applicationEventPublisher.publishEvent(context);
//                }
//        );
    }


    private Specification<RunEntity> createTaskSpecification(String task) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("task"), task);
    }

    private Specification<TaskEntity> createFunctionSpecification(String function) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("function"), function);
    }

    private Specification<TaskEntity> createTaskKindSpecification(String kind) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("kind"), kind);
    }
}
