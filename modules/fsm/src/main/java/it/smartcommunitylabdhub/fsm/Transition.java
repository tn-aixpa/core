package it.smartcommunitylabdhub.fsm;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Transition representation: an event (edge) towards a new state (node).
 * Can define internal logic to handle side effects as callback
 *
 * @param <S> The type of the states.
 * @param <E> The type of the events.
 * @param <C> The type of the context.
 * @param <I> The type of the input
 * @param <R> The type of the return
 */

@RequiredArgsConstructor
@AllArgsConstructor
public class Transition<S, E, C, I> {

    // event definition
    @Getter
    @NotNull
    private final E event;

    // destination state
    @Getter
    @NotNull
    private final S nextState;

    @Nullable
    private TransitionLogic<S, E, C, I, ?> internalLogic;

    /**
     * Get the internal logic associated with this state.
     *
     * @return The internal logic as a StateLogic instance.
     */
    @SuppressWarnings("unchecked")
    public <R> Optional<TransitionLogic<S, E, C, I, R>> getInternalLogic() {
        return Optional.ofNullable((TransitionLogic<S, E, C, I, R>) internalLogic);
    }

    /**
     * Set the internal logic for this state.
     *
     * @param internalLogic The internal logic as a StateLogic instance.
     */
    public <R> void setInternalLogic(@Nullable TransitionLogic<S, E, C, I, R> internalLogic) {
        this.internalLogic = internalLogic;
    }

    /**
     * Builder
     */
    public static class Builder<S, E, C, I> {

        private E event;

        private S nextState;

        private TransitionLogic<S, E, C, I, ?> internalLogic;

        // public Builder() {}

        public Builder<S, E, C, I> event(E event) {
            this.event = event;
            return this;
        }

        public Builder<S, E, C, I> nextState(S nextState) {
            this.nextState = nextState;
            return this;
        }

        public <R> Builder<S, E, C, I> withInternalLogic(@Nullable TransitionLogic<S, E, C, I, R> internalLogic) {
            this.internalLogic = internalLogic;
            return this;
        }

        public Transition<S, E, C, I> build() {
            Transition<S, E, C, I> tx = new Transition<>(event, nextState);
            tx.setInternalLogic(internalLogic);

            return tx;
        }
    }
}
