/**
 * RunStateMachine.java
 * <p>
 * This class is responsible for creating and configuring the StateMachine for managing the state
 * transitions of a Run. It defines the states, events, and transitions specific to the Run entity.
 */

package it.smartcommunitylabdhub.fsm.types;

import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.entities.run.RunState;
import it.smartcommunitylabdhub.commons.services.RunService;
import it.smartcommunitylabdhub.fsm.State;
import it.smartcommunitylabdhub.fsm.StateMachine;
import it.smartcommunitylabdhub.fsm.Transaction;
import it.smartcommunitylabdhub.fsm.enums.RunEvent;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        Map<String, Object> initialContext
    ) {
        // Create a new StateMachine builder with the initial state and context
        StateMachine.Builder<RunState, RunEvent, Map<String, Object>> builder = new StateMachine.Builder<>(
            initialState,
            Optional.of(initialContext)
        );

        // Define states and transitions
        State<RunState, RunEvent, Map<String, Object>> createState = new State<>();
        State<RunState, RunEvent, Map<String, Object>> builtState = new State<>();
        State<RunState, RunEvent, Map<String, Object>> readyState = new State<>();
        State<RunState, RunEvent, Map<String, Object>> runningState = new State<>();
        State<RunState, RunEvent, Map<String, Object>> completedState = new State<>();
        State<RunState, RunEvent, Map<String, Object>> errorState = new State<>();

        createState.addTransaction(new Transaction<>(RunEvent.BUILD, RunState.READY, context -> true));
        builtState.addTransaction(new Transaction<>(RunEvent.BUILD, RunState.READY, context -> true));

        readyState.addTransaction(new Transaction<>(RunEvent.RUNNING, RunState.RUNNING, context -> true));

        readyState.addTransaction(new Transaction<>(RunEvent.PENDING, RunState.READY, context -> true));

        readyState.addTransaction(new Transaction<>(RunEvent.COMPLETED, RunState.COMPLETED, context -> true));

        runningState.addTransaction(new Transaction<>(RunEvent.COMPLETED, RunState.COMPLETED, context -> true));

        // Configure the StateMachine with the defined states and transitions
        builder
            .withState(RunState.CREATED, createState)
            .withExitAction(
                RunState.CREATED,
                context -> {
                    context.ifPresent(c -> {
                        try {
                            // update run state
                            Run runDTO = runService.getRun(c.get("runId").toString());
                            runDTO.getStatus().put("state", RunState.READY.toString());
                            runService.updateRun(runDTO.getId(), runDTO);
                        } catch (NoSuchEntityException nex) {
                            log.error("error updating run: {}", nex.getMessage());
                        }
                    });
                }
            )
            .withState(RunState.BUILT, builtState)
            .withState(RunState.READY, readyState)
            .withState(RunState.RUNNING, runningState)
            .withEntryAction(
                RunState.RUNNING,
                context -> {
                    context.ifPresent(c -> {
                        try {
                            Run runDTO = runService.getRun(c.get("runId").toString());
                            runDTO.getStatus().put("state", RunState.RUNNING.toString());
                            runService.updateRun(runDTO.getId(), runDTO);
                        } catch (NoSuchEntityException nex) {
                            log.error("error updating run: {}", nex.getMessage());
                        }
                    });
                }
            )
            .withState(RunState.COMPLETED, completedState)
            .withErrorState(RunState.ERROR, errorState)
            .withEntryAction(
                RunState.ERROR,
                context -> {
                    context.ifPresent(c -> {
                        try {
                            Run runDTO = runService.getRun(c.get("runId").toString());
                            runDTO.getStatus().put("state", RunState.ERROR.toString());
                            runService.updateRun(runDTO.getId(), runDTO);
                        } catch (NoSuchEntityException nex) {
                            log.error("error updating run: {}", nex.getMessage());
                        }
                    });
                }
            )
            .withStateChangeListener((newState, context) ->
                log.info("State Change Listener: " + newState + ", context: " + context)
            );

        // Build and return the configured StateMachine instance
        return builder.build();
    }
}
