/**
 * RunStateMachine.java
 * <p>
 * This class is responsible for creating and configuring the StateMachine for managing the state
 * transitions of a Run. It defines the states, events, and transitions specific to the Run entity.
 */

package it.smartcommunitylabdhub.fsm.types;

import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.services.RunService;
import it.smartcommunitylabdhub.fsm.Fsm;
import it.smartcommunitylabdhub.fsm.FsmState;
import it.smartcommunitylabdhub.fsm.Transaction;
import it.smartcommunitylabdhub.fsm.enums.RunEvent;
import java.io.Serializable;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RunStateMachineFactory {

    @Autowired
    RunService runService;

    @Autowired
    ApplicationEventPublisher applicationEventPublisher;

    /**
     * Create and configure the StateMachine for managing the state transitions of a Run.
     *
     * @param initialState   The initial state for the StateMachine.
     * @param initialContext The initial context for the StateMachine.
     * @return The configured StateMachine instance.
     */
    public Fsm.Builder<State, RunEvent, Map<String, Serializable>> builder(
        State initialState,
        Map<String, Serializable> initialContext
    ) {
        // Create a new StateMachine builder with the initial state and context
        Fsm.Builder<State, RunEvent, Map<String, Serializable>> builder = new Fsm.Builder<>(
            initialState,
            initialContext
        );

        // Define states and transitions
        FsmState<State, RunEvent, Map<String, Serializable>> createState = new FsmState<>();
        FsmState<State, RunEvent, Map<String, Serializable>> builtState = new FsmState<>();
        FsmState<State, RunEvent, Map<String, Serializable>> readyState = new FsmState<>();
        FsmState<State, RunEvent, Map<String, Serializable>> runningState = new FsmState<>();
        FsmState<State, RunEvent, Map<String, Serializable>> completedState = new FsmState<>();
        FsmState<State, RunEvent, Map<String, Serializable>> errorState = new FsmState<>();
        FsmState<State, RunEvent, Map<String, Serializable>> fsmErrorState = new FsmState<>();

        createState.addTransaction(new Transaction<>(RunEvent.BUILD, State.READY, context -> true));
        builtState.addTransaction(new Transaction<>(RunEvent.BUILD, State.READY, context -> true));
        readyState.addTransaction(new Transaction<>(RunEvent.RUNNING, State.RUNNING, context -> true));
        readyState.addTransaction(new Transaction<>(RunEvent.PENDING, State.READY, context -> true));
        readyState.addTransaction(new Transaction<>(RunEvent.COMPLETED, State.COMPLETED, context -> true));
        runningState.addTransaction(new Transaction<>(RunEvent.COMPLETED, State.COMPLETED, context -> true));

        createState.addTransaction(new Transaction<>(RunEvent.ERROR, State.ERROR, context -> true));
        readyState.addTransaction(new Transaction<>(RunEvent.ERROR, State.ERROR, context -> true));
        runningState.addTransaction(new Transaction<>(RunEvent.ERROR, State.ERROR, context -> true));
        builtState.addTransaction(new Transaction<>(RunEvent.ERROR, State.ERROR, context -> true));

        // Configure the StateMachine with the defined states and transitions
        builder
            .withState(State.CREATED, createState)
            .withState(State.BUILT, builtState)
            .withState(State.READY, readyState)
            .withState(State.RUNNING, runningState)
            .withState(State.COMPLETED, completedState)
            .withState(State.ERROR, errorState)
            .withErrorState(State.FSM_ERROR, fsmErrorState);

        // Return the builder
        return builder;
    }
}
