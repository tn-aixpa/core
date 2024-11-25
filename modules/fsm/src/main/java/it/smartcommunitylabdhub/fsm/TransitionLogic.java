package it.smartcommunitylabdhub.fsm;

import jakarta.annotation.Nullable;
import java.util.Optional;

/**
 * This functional interface represents the internal logic of a transition
 *
 * @param <S> The type of the states.
 * @param <E> The type of the events.
 * @param <C> The type of the context.
 * @param <T> The type of the result from applying the logic.
 */
@FunctionalInterface
public interface TransitionLogic<S, E, C, I, R> {
    /**
     * Apply the internal logic of the state.
     *
     * @param context      The context for the state machine.
     * @param input        The input for the state machine.
     * @param stateMachine The state machine instance.
     * @return The optional result from applying the logic.
     */
    Optional<R> apply(S currentState, S nextState, E event, @Nullable C context, @Nullable I input);
}
