/**
 * Transaction.java
 * <p>
 * This class represents a transition in the State Machine. It defines the event, next state, guard,
 * and auto-flag for the transition.
 *
 * @param <S> The type of the states.
 * @param <E> The type of the events.
 * @param <C> The type of the context.
 */

package it.smartcommunitylabdhub.fsm;

import jakarta.annotation.Nullable;
import java.util.Optional;
import java.util.function.BiPredicate;
import lombok.Getter;

public class Transaction<S, E, C, R> {

    @Getter
    private E event;

    @Getter
    private S nextState;

    @Getter
    private BiPredicate<C, C> guard;

    @Nullable
    private StateLogic<S, E, C, R> internalLogic;

    public Transaction(E event, S nextState, BiPredicate<C, C> guard) {
        this.event = event;
        this.nextState = nextState;
        this.guard = guard;
        internalLogic = null;
    }

    /**
     * Get the internal logic associated with this state.
     *
     * @return The internal logic as a StateLogic instance.
     */
    public Optional<StateLogic<S, E, C, R>> getInternalLogic() {
        return Optional.ofNullable(internalLogic);
    }

    /**
     * Set the internal logic for this state.
     *
     * @param internalLogic The internal logic as a StateLogic instance.
     */
    public <T> void setInternalLogic(@Nullable StateLogic<S, E, C, T> internalLogic) {
        this.internalLogic = (StateLogic<S, E, C, R>) internalLogic; // Type casting
    }
}
