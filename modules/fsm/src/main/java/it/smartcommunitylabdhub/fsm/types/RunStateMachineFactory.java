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
import java.util.List;
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
    public Fsm<State, RunEvent, Map<String, Serializable>> builder(
        State initialState,
        Map<String, Serializable> initialContext
    ) {
        // Create a new StateMachine builder with the initial state and context
        Fsm.Builder<State, RunEvent, Map<String, Serializable>> builder = new Fsm.Builder<>(
            initialState,
            initialContext
        );

        // Configure the StateMachine with the defined states and transitions
        builder
            .withState(State.CREATED, new FsmState<>())
            .withTransactions(
                List.of(
                    new Transaction<>(RunEvent.BUILD, State.READY, (context, input) -> true),
                    new Transaction<>(RunEvent.ERROR, State.ERROR, (context, input) -> true)
                )
            )
            .withFsm()
            .withState(State.BUILT, new FsmState<>())
            .withTransactions(
                List.of(
                    new Transaction<>(RunEvent.BUILD, State.READY, (context, input) -> true),
                    new Transaction<>(RunEvent.ERROR, State.ERROR, (context, input) -> true)
                )
            )
            .withFsm()
            .withState(State.READY, new FsmState<>())
            .withTransactions(
                List.of(
                    new Transaction<>(RunEvent.RUNNING, State.RUNNING, (context, input) -> true),
                    new Transaction<>(RunEvent.PENDING, State.READY, (context, input) -> true),
                    new Transaction<>(RunEvent.COMPLETED, State.COMPLETED, (context, input) -> true),
                    new Transaction<>(RunEvent.ERROR, State.ERROR, (context, input) -> true)
                )
            )
            .withFsm()
            .withState(State.RUNNING, new FsmState<>())
            .withTransactions(
                List.of(
                    new Transaction<>(RunEvent.COMPLETED, State.COMPLETED, (context, input) -> true),
                    new Transaction<>(RunEvent.ERROR, State.ERROR, (context, input) -> true)
                )
            )
            .withFsm()
            .withState(State.COMPLETED, new FsmState<>())
            .withFsm()
            .withState(State.ERROR, new FsmState<>())
            .withFsm()
            .withErrorState(State.FSM_ERROR, new FsmState<>());

        // Return the builder
        return builder.build();
    }
}
