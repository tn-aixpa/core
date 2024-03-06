package it.smartcommunitylabdhub.core.components.run;

import it.smartcommunitylabdhub.commons.events.RunChangedEvent;
import it.smartcommunitylabdhub.commons.events.RunMonitorObject;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.infrastructure.Runnable;
import it.smartcommunitylabdhub.commons.infrastructure.Runtime;
import it.smartcommunitylabdhub.commons.models.entities.function.FunctionBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseStatus;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.runtimes.RuntimeFactory;
import it.smartcommunitylabdhub.core.models.builders.run.RunDTOBuilder;
import it.smartcommunitylabdhub.core.models.builders.run.RunEntityBuilder;
import it.smartcommunitylabdhub.core.models.entities.log.LogEntity;
import it.smartcommunitylabdhub.core.models.entities.run.RunEntity;
import it.smartcommunitylabdhub.core.repositories.LogRepository;
import it.smartcommunitylabdhub.core.repositories.RunRepository;
import it.smartcommunitylabdhub.core.services.EntityService;
import it.smartcommunitylabdhub.fsm.Fsm;
import it.smartcommunitylabdhub.fsm.enums.RunEvent;
import it.smartcommunitylabdhub.fsm.types.RunStateMachineFactory;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Slf4j
@Component
public class RunManager {

    private final RunStateMachineFactory runStateMachine;

    private final RunRepository runRepository;

    private final LogRepository logRepository;

    private final EntityService<Run, RunEntity> entityService;

    private final RunDTOBuilder runDTOBuilder;

    private final RunEntityBuilder runEntityBuilder;

    private final RuntimeFactory runtimeFactory;

    public RunManager(
            RunStateMachineFactory runStateMachine,
            RunRepository runRepository,
            LogRepository logRepository,
            EntityService<Run, RunEntity> entityService,
            RunDTOBuilder runDTOBuilder,
            RunEntityBuilder runEntityBuilder,
            RuntimeFactory runtimeFactory
    ) {
        this.runStateMachine = runStateMachine;
        this.runRepository = runRepository;
        this.logRepository = logRepository;
        this.entityService = entityService;
        this.runDTOBuilder = runDTOBuilder;
        this.runEntityBuilder = runEntityBuilder;
        this.runtimeFactory = runtimeFactory;
    }


    public Run build(@NotNull Run run) throws NoSuchEntityException {
        // GET state machine, init state machine with status

        Fsm<State, RunEvent, Map<String, Serializable>> fsm = createFsm(run);

        // Pass internal logic to state machine
        fsm.getState(State.CREATED)
                .getTransaction(RunEvent.BUILD)
                .setInternalLogic((context, input, fsmInstance) -> {
                    try {

                        RunBaseSpec runSpec = new RunBaseSpec();
                        runSpec.configure(run.getSpec());

                        if (!Optional.ofNullable(runSpec.getLocalExecution()).orElse(Boolean.FALSE)) {
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
                        //TODO fix this exception.
                    } catch (IllegalArgumentException e) {
                        throw new RuntimeException(e);
                    }
                });


        // TODO move build+exec to dedicated methods and handle state changes!
        Optional<RunBaseSpec> runSpecBuilt = fsm.goToState(State.BUILT, null);
        runSpecBuilt.ifPresent(
                spec -> {
                    // Update run spec
                    run.setSpec(spec.toMap());

                    // Update run state to BUILT
                    run.getStatus().put("state", State.BUILT.toString());

                    if (log.isTraceEnabled()) {
                        log.trace("built run: {}", run);
                    }
                });

        entityService.update(run.getId(), run);
        return run;
    }


    public Run run(@NotNull Run run) {
        //        fsm = createFsm(run).getState(State.BUILT).setInternalLogic((context, input, fsmInstance) -> {
//        //TODO move build+exec to dedicated methods and handle state changes!
//        if (Optional.ofNullable(runSpec.getLocalExecution()).orElse(Boolean.FALSE).booleanValue() == false) {
//            // Retrieve Runtime and build run
//            Runtime<? extends FunctionBaseSpec, ? extends RunBaseSpec, ? extends Runnable> runtime =
//                    runtimeFactory.getRuntime(function.getKind());
//
//            // Create Runnable
//            Runnable runnable = runtime.run(run);
//
//            // Dispatch Runnable
//            eventPublisher.publishEvent(runnable);


        // RETURN runnable
//        }
////        }
//        });

//        try {
//            runnable = fsm.goToState(State.READY);
//
//            // if goToState success save this
//            // entityService.update(run.getId(), run);
//        } catch (InvalidTransitionException e) {
//            // log error
//        }
        return run;
    }


    public Run stop(@NotNull Run run) {
        /// check the one above...

        return run;
    }

    @Async
    @EventListener
    public void onRunning(RunChangedEvent event) {
        // Retrieve the RunMonitorObject from the event
        RunMonitorObject runMonitorObject = event.getRunMonitorObject();

        // Find the related RunEntity
        runRepository
                .findById(runMonitorObject.getRunId())
                .stream()
                .filter(runEntity -> !runEntity.getState().name().equals(runMonitorObject.getStateId()))
                .findAny()
                .ifPresentOrElse(
                        runEntity -> {
                            // Try to move forward state machine based on current state
                            createFsm(runEntity).goToState(State.valueOf(runMonitorObject.getStateId()), null);
                            System.out.println("jello");
                        },
                        () -> {
                            error(runMonitorObject.getRunId());
                            log.error("Run with id {} not found", runMonitorObject.getRunId());
                        }
                );
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

        //TODO review this part
        RunEntity runEntity = runEntityBuilder.build(run);
        RunBaseStatus runBaseStatus = new RunBaseStatus();
        runBaseStatus.configure(run.getStatus());

        // Create state machine context
        Map<String, Serializable> ctx = new HashMap<>();
        ctx.put("run", run);

        // Initialize state machine
        Fsm<State, RunEvent, Map<String, Serializable>> fsm = runStateMachine.builder(
                State.valueOf(runEntity.getState().toString()), ctx
        );

        // On state change delegate state machine to update the run
        fsm.setStateChangeListener((state, context) -> {
            Assert.notNull(context, "Context cannot be null");

            Run contextRun = (Run) context.get("run");
            RunEntity contextRunEntity = runEntityBuilder.build(contextRun);

            // Update entity state and push back on context
            contextRunEntity.setState(state);
            context.put("run", contextRun);

            // Save entity
            runRepository.save(contextRunEntity);
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
}
