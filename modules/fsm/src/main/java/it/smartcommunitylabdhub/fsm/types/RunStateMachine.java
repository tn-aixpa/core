/**
 * RunStateMachine.java
 * <p>
 * This class is responsible for creating and configuring the StateMachine for managing the state
 * transitions of a Run. It defines the states, events, and transitions specific to the Run entity.
 */

package it.smartcommunitylabdhub.fsm.types;

import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.services.RunService;
import it.smartcommunitylabdhub.fsm.Fsm;
import it.smartcommunitylabdhub.fsm.FsmState;
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
    public Fsm<State, RunEvent, Map<String, Object>> create(
            State initialState,
            Map<String, Object> initialContext
    ) {
        // Create a new StateMachine builder with the initial state and context
        Fsm.Builder<State, RunEvent, Map<String, Object>> builder = new Fsm.Builder<>(
                initialState,
                Optional.of(initialContext)
        );

        // Define states and transitions
        FsmState<State, RunEvent, Map<String, Object>> createState = new FsmState<>();
        FsmState<State, RunEvent, Map<String, Object>> builtState = new FsmState<>();
        FsmState<State, RunEvent, Map<String, Object>> readyState = new FsmState<>();
        FsmState<State, RunEvent, Map<String, Object>> runningState = new FsmState<>();
        FsmState<State, RunEvent, Map<String, Object>> completedState = new FsmState<>();
        FsmState<State, RunEvent, Map<String, Object>> errorState = new FsmState<>();

        createState.addTransaction(new Transaction<>(RunEvent.BUILD, State.READY, context -> true));
        builtState.addTransaction(new Transaction<>(RunEvent.BUILD, State.READY, context -> true));

        readyState.addTransaction(new Transaction<>(RunEvent.RUNNING, State.RUNNING, context -> true));

        readyState.addTransaction(new Transaction<>(RunEvent.PENDING, State.READY, context -> true));

        readyState.addTransaction(new Transaction<>(RunEvent.COMPLETED, State.COMPLETED, context -> true));

        runningState.addTransaction(new Transaction<>(RunEvent.COMPLETED, State.COMPLETED, context -> true));

        // Configure the StateMachine with the defined states and transitions
        builder
                .withState(State.CREATED, createState)
                .withExitAction(
                        State.CREATED,
                        context -> {
                            context.ifPresent(c -> {
                                // update run state
                                Run runDTO = runService.getRun(c.get("runId").toString());
                                runDTO.getStatus().put("state", State.READY.toString());
                                runService.updateRun(runDTO, runDTO.getId());
                            });
                        }
                )
                .withState(State.BUILT, builtState)
                .withState(State.READY, readyState)
                .withState(State.RUNNING, runningState)
                .withEntryAction(
                        State.RUNNING,
                        context -> {
                            context.ifPresent(c -> {
                                Run runDTO = runService.getRun(c.get("runId").toString());
                                runDTO.getStatus().put("state", State.RUNNING.toString());

                                runService.updateRun(runDTO, runDTO.getId());
                            });
                        }
                )
                .withState(State.COMPLETED, completedState)
                .withErrorState(State.ERROR, errorState)
                .withEntryAction(
                        State.ERROR,
                        context -> {
                            context.ifPresent(c -> {
                                Run runDTO = runService.getRun(c.get("runId").toString());
                                runDTO.getStatus().put("state", State.ERROR.toString());
                                runService.updateRun(runDTO, runDTO.getId());
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
