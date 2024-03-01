/**
 * State.java
 * <p>
 * This class represents a state in the State Machine. It contains information about the entry
 * action, exit action, internal logic, and transactions associated with the state.
 *
 * @param <S> The type of the states.
 * @param <E> The type of the events.
 * @param <C> The type of the context.
 */

package it.smartcommunitylabdhub.fsm;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FsmState<S, E, C> {

    private Optional<StateLogic<S, E, C, ?>> internalLogic;
    private Map<E, Transaction<S, E, C>> transactions;

    public FsmState() {
        internalLogic = Optional.empty();
        transactions = new HashMap<>();
    }

    /**
     * Get the internal logic associated with this state.
     *
     * @return The internal logic as a StateLogic instance.
     */
    public Optional<StateLogic<S, E, C, ?>> getInternalLogic() {
        return internalLogic;
    }

    /**
     * Set the internal logic for this state.
     *
     * @param internalLogic The internal logic as a StateLogic instance.
     * @param <T>           The type of the result from the internal logic.
     */
    public <T> void setInternalLogic(StateLogic<S, E, C, T> internalLogic) {
        this.internalLogic = Optional.ofNullable(internalLogic);
    }

    /**
     * Add a transaction associated with this state.
     *
     * @param transaction The transaction to add.
     */
    public void addTransaction(Transaction<S, E, C> transaction) {
        transactions.put(transaction.getEvent(), transaction);
    }

    /**
     * Get the transactions associated with this state.
     *
     * @return The map of transactions, where the key is the event and the value is the
     * corresponding transaction.
     */
    public Map<E, Transaction<S, E, C>> getTransactions() {
        return transactions;
    }

    /**
     * Retrieves the transition event associated with a given next state.
     *
     * @param nextState The next state for which to retrieve the transition event.
     * @return An Optional containing the transition event if found, or an empty Optional if not
     * found.
     */
    public Optional<E> getTransitionEvent(S nextState) {
        // Iterate over the transitions to find the event associated with the next state
        for (Map.Entry<E, Transaction<S, E, C>> entry : transactions.entrySet()) {
            if (entry.getValue().getNextState().equals(nextState)) {
                return Optional.of(entry.getKey()); // Found the matching event
            }
        }
        return Optional.empty(); // No matching event found
    }


    public static class StateBuilder<S, E, C> {

        private final S state;
        private final Fsm.Builder<S, E, C> parentBuilder;
        private final FsmState<S, E, C> stateDefinition;

        public StateBuilder(S state, Fsm.Builder<S, E, C> parentBuilder, FsmState<S, E, C> stateDefinition) {
            this.state = state;
            this.parentBuilder = parentBuilder;
            this.stateDefinition = stateDefinition;
        }

        public StateBuilder<S, E, C> withInternalLogic(StateLogic<S, E, C, ?> internalLogic) {
            stateDefinition.setInternalLogic(internalLogic);
            return this;
        }

        public StateBuilder<S, E, C> withTransaction(Transaction<S, E, C> transaction) {
            stateDefinition.addTransaction(transaction);
            return this;
        }

        public Fsm.Builder<S, E, C> withFsm() {
            parentBuilder.withState(state, stateDefinition);
            return parentBuilder;
        }
    }
}
