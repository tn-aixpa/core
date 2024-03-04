/**
 * StateLogic.java
 * <p>
 * This functional interface represents the internal logic of a state in the State Machine.
 *
 * @param <S> The type of the states.
 * @param <E> The type of the events.
 * @param <C> The type of the context.
 * @param <T> The type of the result from applying the logic.
 */

package it.smartcommunitylabdhub.fsm;

import jakarta.annotation.Nullable;
import java.util.Optional;

@FunctionalInterface
public interface StateLogic<S, E, C, T> {
    /**
     * Apply the internal logic of the state.
     *
     * @param context      The context for the state machine.
     * @param input        The input for the state machine.
     * @param stateMachine The state machine instance.
     * @return The optional result from applying the logic.
     */
    Optional<T> applyLogic(@Nullable C context, @Nullable C input, Fsm<S, E, C> stateMachine);
}
