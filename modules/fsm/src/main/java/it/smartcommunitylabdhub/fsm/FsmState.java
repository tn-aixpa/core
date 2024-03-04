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

import jakarta.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class FsmState<S, E, C> {

    private final Map<E, Transaction<S, E, C>> transactions;
    @Nullable
    private StateLogic<S, E, C, ?> internalLogic;
    private Consumer<C> entryAction;
    private Consumer<C> exitAction;

    /**
     * Constructs a new FsmState object with default values.
     */
    public FsmState() {
        internalLogic = null;
        transactions = new HashMap<>();
    }

    /**
     * Get the internal logic associated with this state.
     *
     * @return The internal logic as a StateLogic instance.
     */
    public Optional<StateLogic<S, E, C, ?>> getInternalLogic() {
        return Optional.ofNullable(internalLogic);
    }

    /**
     * Set the internal logic for this state.
     *
     * @param internalLogic The internal logic as a StateLogic instance.
     * @param <T>           The type of the result from the internal logic.
     */
    public <T> void setInternalLogic(@Nullable StateLogic<S, E, C, T> internalLogic) {
        this.internalLogic = internalLogic;
    }

    /**
     * Add a transaction associated with this state.
     *
     * @param transaction The transaction to add.
     */
    public void setTransaction(Transaction<S, E, C> transaction) {
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

    public void setTransactions(List<Transaction<S, E, C>> transactionList) {
        transactionList.forEach(transaction -> transactions.put(transaction.getEvent(), transaction));
    }

    /**
     * Get the entry action associated with this state.
     *
     * @return The entry action as a Consumer instance.
     */
    public Consumer<C> getEntryAction() {
        return entryAction;
    }

    /**
     * Add an entry action associated with this state.
     *
     * @param action The entry action as a Consumer instance.
     */
    public void setEntryAction(Consumer<C> action) {
        entryAction = action;
    }

    /**
     * Get the exit action associated with this state.
     *
     * @return The exit action as a Consumer instance.
     */
    public Consumer<C> getExitAction() {
        return exitAction;
    }

    /**
     * Add an exit action associated with this state.
     *
     * @param action The exit action as a Consumer instance.
     */
    public void setExitAction(Consumer<C> action) {
        exitAction = action;
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

    /**
     * A builder class for constructing FsmState objects.
     *
     * @param <S> The type of the states.
     * @param <E> The type of the events.
     * @param <C> The type of the context.
     */
    public static class StateBuilder<S, E, C> {

        private final S state;
        private final Fsm.Builder<S, E, C> parentBuilder;
        private final FsmState<S, E, C> stateDefinition;

        /**
         * Constructs a new StateBuilder object.
         *
         * @param state           The state to build.
         * @param parentBuilder   The parent builder instance.
         * @param stateDefinition The state definition to modify.
         */
        public StateBuilder(S state, Fsm.Builder<S, E, C> parentBuilder, FsmState<S, E, C> stateDefinition) {
            this.state = state;
            this.parentBuilder = parentBuilder;
            this.stateDefinition = stateDefinition;
        }

        /**
         * Set the internal logic for this state.
         *
         * @param internalLogic The internal logic as a StateLogic instance.
         * @return The StateBuilder instance.
         */
        public StateBuilder<S, E, C> withInternalLogic(StateLogic<S, E, C, ?> internalLogic) {
            stateDefinition.setInternalLogic(internalLogic);
            return this;
        }

        /**
         * Add a transaction associated with this state.
         *
         * @param transaction The transaction to add.
         * @return The StateBuilder instance.
         */
        public StateBuilder<S, E, C> withTransaction(Transaction<S, E, C> transaction) {
            stateDefinition.setTransaction(transaction);
            return this;
        }

        /**
         * Add a transaction associated with this state.
         *
         * @param transactions List of transactions
         * @return The StateBuilder instance.
         */
        public StateBuilder<S, E, C> withTransactions(List<Transaction<S, E, C>> transactions) {
            stateDefinition.setTransactions(transactions);
            return this;
        }

        /**
         * Add an entry action associated with this state.
         *
         * @param action The entry action as a Consumer instance.
         * @return The StateBuilder instance.
         */
        public StateBuilder<S, E, C> withEntryAction(Consumer<C> action) {
            stateDefinition.setEntryAction(action);
            return this;
        }

        /**
         * Add an exit action associated with this state.
         *
         * @param action The exit action as a Consumer instance.
         * @return The StateBuilder instance.
         */
        public StateBuilder<S, E, C> withExitAction(Consumer<C> action) {
            stateDefinition.setExitAction(action);
            return this;
        }

        /**
         * Finalize the state definition and return the parent builder.
         *
         * @return The parent builder instance.
         */
        public Fsm.Builder<S, E, C> withFsm() {
            parentBuilder.withState(state, stateDefinition);
            return parentBuilder;
        }
    }
}
