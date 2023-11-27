/**
 * RunStateMachine.java
 * <p>
 * This class is responsible for creating and configuring the StateMachine for managing the state
 * transitions of a Run. It defines the states, events, and transitions specific to the Run entity.
 */

package it.smartcommunitylabdhub.core.components.fsm.types;

import it.smartcommunitylabdhub.core.components.fsm.State;
import it.smartcommunitylabdhub.core.components.fsm.StateMachine;
import it.smartcommunitylabdhub.core.components.fsm.Transaction;
import it.smartcommunitylabdhub.core.components.fsm.enums.RunEvent;
import it.smartcommunitylabdhub.core.components.fsm.enums.RunState;
import it.smartcommunitylabdhub.core.models.entities.run.Run;
import it.smartcommunitylabdhub.core.services.interfaces.RunService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class RunStateMachine {

    @Autowired
    RunService runService;

    /**
     * Create and configure the StateMachine for managing the state transitions of a Run.
     *
     * @param initialState   The initial state for the StateMachine.
     * @param initialContext The initial context for the StateMachine.
     * @return The configured StateMachine instance.
     */
    public StateMachine<RunState, RunEvent, Map<String, Object>> create(
            RunState initialState,
            Map<String, Object> initialContext) {

        // Create a new StateMachine builder with the initial state and context
        StateMachine.Builder<RunState, RunEvent, Map<String, Object>> builder =
                new StateMachine.Builder<>(
                        initialState, Optional.of(initialContext));

        // Define states and transitions
        State<RunState, RunEvent, Map<String, Object>> createState = new State<>();
        State<RunState, RunEvent, Map<String, Object>> builtState = new State<>();
        State<RunState, RunEvent, Map<String, Object>> readyState = new State<>();
        State<RunState, RunEvent, Map<String, Object>> runningState = new State<>();
        State<RunState, RunEvent, Map<String, Object>> completedState = new State<>();
        State<RunState, RunEvent, Map<String, Object>> errorState = new State<>();


        createState.addTransaction(
                new Transaction<>(RunEvent.BUILD, RunState.READY,
                        (context) -> true));
        builtState.addTransaction(
                new Transaction<>(RunEvent.BUILD, RunState.READY,
                        (context) -> true));

        readyState.addTransaction(
                new Transaction<>(RunEvent.RUNNING, RunState.RUNNING,
                        (context) -> true));

        readyState.addTransaction(
                new Transaction<>(RunEvent.PENDING, RunState.READY,
                        (context) -> true));

        readyState.addTransaction(new Transaction<>(RunEvent.COMPLETED, RunState.COMPLETED,
                (context) -> true));

        runningState.addTransaction(
                new Transaction<>(RunEvent.COMPLETED, RunState.COMPLETED,
                        (context) -> true));

        // Configure the StateMachine with the defined states and transitions
        builder.withState(RunState.CREATED, createState)
                .withExitAction(RunState.CREATED, (context) -> {
                    context.ifPresent(c -> {
                        // update run state
                        Run runDTO = runService
                                .getRun(c.get("runId")
                                        .toString());
                        runDTO.setState(RunState.READY.toString());
                        runService.updateRun(runDTO, runDTO.getId());
                    });

                })
                .withState(RunState.BUILT, builtState)
                .withState(RunState.READY, readyState)
                .withState(RunState.RUNNING, runningState)
                .withEntryAction(RunState.RUNNING, (context) -> {
                    context.ifPresent(c -> {
                        Run runDTO = runService
                                .getRun(c.get("runId")
                                        .toString());
                        runDTO.setState(RunState.RUNNING.toString());
                        runService.updateRun(runDTO, runDTO.getId());
                    });

                })
                .withState(RunState.COMPLETED, completedState)
                .withErrorState(RunState.ERROR, errorState)
                .withEntryAction(RunState.ERROR, (context) -> {
                    context.ifPresent(c -> {
                        Run runDTO = runService
                                .getRun(c.get("runId").toString());
                        runDTO.setState(RunState.ERROR.toString());
                        runService.updateRun(runDTO, runDTO.getId());
                    });

                })
                .withStateChangeListener((newState, context) -> log
                        .info("State Change Listener: " + newState
                                + ", context: " + context));

        // Build and return the configured StateMachine instance
        return builder.build();
    }
}
