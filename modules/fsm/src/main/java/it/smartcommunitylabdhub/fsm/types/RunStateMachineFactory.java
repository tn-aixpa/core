/**
 * RunStateMachine.java
 * <p>
 * This class is responsible for creating and configuring the StateMachine for managing the state
 * transitions of a Run. It defines the states, events, and transitions specific to the Run entity.
 */

package it.smartcommunitylabdhub.fsm.types;

import it.smartcommunitylabdhub.commons.models.enums.State;
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
                .withState(
                        State.CREATED,
                        new FsmState.StateBuilder<State, RunEvent, Map<String, Serializable>>(State.CREATED)
                                .withTransactions(
                                        List.of(
                                                new Transaction<>(RunEvent.DELETING, State.DELETED, (context, input) -> true),
                                                new Transaction<>(RunEvent.BUILD, State.BUILT, (context, input) -> true),
                                                new Transaction<>(RunEvent.ERROR, State.ERROR, (context, input) -> true)
                                        )
                                )
                                .build()
                )
                .withState(
                        State.BUILT,
                        new FsmState.StateBuilder<State, RunEvent, Map<String, Serializable>>(State.BUILT)
                                .withTransactions(
                                        List.of(
                                                new Transaction<>(RunEvent.DELETING, State.DELETED, (context, input) -> true),
                                                new Transaction<>(RunEvent.RUN, State.READY, (context, input) -> true),
                                                new Transaction<>(RunEvent.ERROR, State.ERROR, (context, input) -> true)
                                        )
                                )
                                .build()
                )
                .withState(
                        State.READY,
                        new FsmState.StateBuilder<State, RunEvent, Map<String, Serializable>>(State.READY)
                                .withTransactions(
                                        List.of(
                                                new Transaction<>(RunEvent.EXECUTE, State.RUNNING, (context, input) -> true),
                                                new Transaction<>(RunEvent.PENDING, State.READY, (context, input) -> true),
//                            new Transaction<>(RunEvent.COMPLETE, State.COMPLETED, (context, input) -> true),
                                                new Transaction<>(RunEvent.ERROR, State.ERROR, (context, input) -> true),
                                                new Transaction<>(RunEvent.DELETING, State.DELETED, (context, input) -> true)
                                        )
                                )
                                .build()
                )
                .withState(
                        State.STOP,
                        new FsmState.StateBuilder<State, RunEvent, Map<String, Serializable>>(State.STOP)
                                .withTransactions(
                                        List.of(
                                                new Transaction<>(RunEvent.STOP, State.STOPPED, (context, input) -> true),
                                                new Transaction<>(RunEvent.DELETING, State.DELETED, (context, input) -> true)
                                        )
                                )
                                .build()
                )
                .withState(
                        State.STOPPED,
                        new FsmState.StateBuilder<State, RunEvent, Map<String, Serializable>>(State.STOPPED)
                                .withTransactions(
                                        List.of(
                                                new Transaction<>(RunEvent.ERROR, State.ERROR, (context, input) -> true),
                                                new Transaction<>(RunEvent.DELETING, State.DELETED, (context, input) -> true)
                                        )
                                )
                                .build()
                )
                .withState(
                        State.RUNNING,
                        new FsmState.StateBuilder<State, RunEvent, Map<String, Serializable>>(State.RUNNING)
                                .withTransactions(
                                        List.of(
                                                new Transaction<>(RunEvent.LOOP, State.RUNNING, (context, input) -> true),
                                                new Transaction<>(RunEvent.COMPLETE, State.COMPLETED, (context, input) -> true),
                                                new Transaction<>(RunEvent.ERROR, State.ERROR, (context, input) -> true),
                                                new Transaction<>(RunEvent.STOP, State.STOP, (context, input) -> true),
                                                new Transaction<>(RunEvent.DELETING, State.DELETED, (context, input) -> true)
                                        )
                                )
                                .build()
                )
                .withState(
                        State.COMPLETED,
                        new FsmState.StateBuilder<State, RunEvent, Map<String, Serializable>>(State.COMPLETED).build()
                )
                .withState(
                        State.ERROR,
                        new FsmState.StateBuilder<State, RunEvent, Map<String, Serializable>>(State.ERROR).build()
                )
                .build();

        // Return the builder

        return builder.build();
    }
}
