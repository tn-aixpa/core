package it.smartcommunitylabdhub.core.components.run;

import it.smartcommunitylabdhub.commons.events.RunChangedEvent;
import it.smartcommunitylabdhub.commons.events.RunMonitorObject;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.core.models.entities.log.LogEntity;
import it.smartcommunitylabdhub.core.repositories.LogRepository;
import it.smartcommunitylabdhub.core.repositories.RunRepository;
import it.smartcommunitylabdhub.fsm.Fsm;
import it.smartcommunitylabdhub.fsm.enums.RunEvent;
import it.smartcommunitylabdhub.fsm.types.RunStateMachine;
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


    private final RunStateMachine runStateMachine;

    private final RunRepository runRepository;

    private final LogRepository logRepository;

    public RunManager(RunStateMachine runStateMachine,
                      RunRepository runRepository,
                      LogRepository logRepository) {
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
        runRepository.findById(runMonitorObject.getRunId())
                .stream()
                .filter(runEntity -> !runEntity.getState().name().equals(runMonitorObject.getStateId()))
                .findAny()
                .ifPresentOrElse(
                        runEntity -> {
                            // Initialize state machine based on run entity State.
                            Fsm<State, RunEvent, Map<String, Serializable>> fsm = runStateMachine.create(
                                    State.valueOf(runEntity.getState().name()),
                                    Map.of("runId", runEntity.getId())
                            );

                            // Try to move forward state machine based on current state
                            fsm.goToState(State.valueOf(runMonitorObject.getStateId()), Optional.empty());
                        },
                        () -> log.error("Run with id {} not found", runMonitorObject.getRunId())
                );

    }


    @Async
    @EventListener
    public void log(LogEntity logEntity) {
        logRepository.save(logEntity);
    }
}
