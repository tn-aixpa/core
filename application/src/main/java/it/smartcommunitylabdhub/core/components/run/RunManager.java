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

    public void error(String id) {
    }

    private Fsm<State, RunEvent, Map<String, Serializable>> createFsm(RunEntity runEntity) {
        // Initialize state machine based on run entity State.
        Fsm.Builder<State, RunEvent, Map<String, Serializable>> fsmBuilder = runStateMachine.builder(
                State.valueOf(runEntity.getState().name()),
                Map.of("runId", runEntity.getId())
        );

        return fsmBuilder
                .withEventListener(
                        RunEvent.ERROR,
                        (context, input) -> {
                            // notifiy log when error happend
                            // applicationEventPublisher.publishEvent(context);
                        }
                )
                .withStateChangeListener((state, context) ->
                        {
                            runEntity.setState(state);
                            runRepository.save(runEntity);
                            log.info("State Change Listener: " + state + ", context: " + context);
                        }
                ).build();

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////         OLD ACTION FOR FMS       ////////////////////////////////////////
//                    .withExitAction(
//                State.CREATED,
//                context -> {
//                    context.ifPresent(c -> {
//                        // update run state
//                        runEntity.setState(State.READY);
//                        runRepository.save(runEntity);
//                    });
//                }
//            )
//            .withEntryAction(
//                State.RUNNING,
//                context -> {
//                    context.ifPresent(c -> {
//                        runEntity.setState(State.RUNNING);
//                        runRepository.save(runEntity);
//                    });
//                }
//            )
//            .withEntryAction(
//                State.ERROR,
//                context -> {
//                    context.ifPresent(c -> {
//                        runEntity.setState(State.ERROR);
//                        runRepository.save(runEntity);
//                    });
//                }
//            )
    }
}
