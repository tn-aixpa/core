package it.smartcommunitylabdhub.core.components.run;

import it.smartcommunitylabdhub.commons.events.RunChangedEvent;
import it.smartcommunitylabdhub.commons.events.RunMonitorObject;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.core.models.entities.log.LogEntity;
import it.smartcommunitylabdhub.core.models.entities.run.RunEntity;
import it.smartcommunitylabdhub.core.repositories.LogRepository;
import it.smartcommunitylabdhub.core.repositories.RunRepository;
import it.smartcommunitylabdhub.fsm.Fsm;
import it.smartcommunitylabdhub.fsm.enums.RunEvent;
import it.smartcommunitylabdhub.fsm.types.RunStateMachineFactory;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RunManager {

    private final RunStateMachineFactory runStateMachine;

    private final RunRepository runRepository;

    private final LogRepository logRepository;

    public RunManager(
        RunStateMachineFactory runStateMachine,
        RunRepository runRepository,
        LogRepository logRepository
    ) {
        this.runStateMachine = runStateMachine;
        this.runRepository = runRepository;
        this.logRepository = logRepository;
    }

    @Async
    @EventListener
    public void monitor(RunChangedEvent event) {
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
                    createFsm(runEntity).goToState(State.valueOf(runMonitorObject.getStateId()), Optional.empty());
                },
                () -> {
                    error(runMonitorObject.getRunId());
                    log.error("Run with id {} not found", runMonitorObject.getRunId());
                }
            );
    }

    @Async
    @EventListener
    public void log(LogEntity logEntity) {
        logRepository.save(logEntity);
    }

    public void error(String id) {}

    private Fsm<State, RunEvent, Map<String, Serializable>> createFsm(RunEntity runEntity) {
        // INITIALIZE STATE MACHINE AND TRANSACTION
        Fsm<State, RunEvent, Map<String, Serializable>> fsm = runStateMachine.builder(
            State.valueOf(runEntity.getState().name()),
            Map.of("runId", runEntity.getId())
        );

        // DEFINING CUSTOM STATE MACHINE BEHAVIOUR
        fsm
            .getState(State.READY)
            .setExitAction(context -> {
                log.info("EXITING FROM READY STATE");
            });
        fsm
            .getState(State.READY)
            .setInternalLogic((context, input, fsmInstance) -> {
                log.info("READY INTERNAL LOGIC");
                return Optional.of("Hello");
            });
        fsm
            .getState(State.RUNNING)
            .setInternalLogic((context, input, fsmInstance) -> {
                log.info("RUNNING INTERNAL LOGIC");
                return Optional.of("Hello");
            });
        fsm.setEventListener(
            RunEvent.ERROR,
            (context, input) -> {
                // do something
                // notifiy log when error happend
                // applicationEventPublisher.publishEvent(context);
            }
        );
        fsm.setStateChangeListener((state, context) -> {
            // Update entity state
            runEntity.setState(state);

            // Save entity
            runRepository.save(runEntity);
            log.info("State Change Listener: " + state + ", context: " + context);
        });

        return fsm;
    }
}
