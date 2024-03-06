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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.Setter;

public class FsmState<S, E, C> {


    @Getter
    private final List<Transaction<S, E, C, ?>> transactions;
    private final S state;
    @Getter
    @Setter
    private Consumer<C> entryAction;
    @Getter
    @Setter
    private Consumer<C> exitAction;


    public FsmState() {
        this.transactions = new ArrayList<>();
        this.entryAction = null;
        this.exitAction = null;
        this.state = null;
    }

    public FsmState(S state, List<Transaction<S, E, C, ?>> transactions, Consumer<C> entryAction, Consumer<C> exitAction) {
        this.transactions = Collections.unmodifiableList(transactions);
        this.entryAction = entryAction;
        this.exitAction = exitAction;
        this.state = state;
    }


    public Transaction<S, E, C, ?> getTransaction(E event) {
        for (Transaction<S, E, C, ?> transaction : transactions) {
            if (transaction.getEvent().equals(event)) {
                return transaction;
            }
        }
        return null;
    }


    /**
     * Retrieves the transition event associated with a given next state.
     *
     * @param nextState The next state for which to retrieve the transition event.
     * @return An Optional containing the transition event if found, or an empty Optional if not
     * found.
     */
    public <R> Optional<Transaction<S, E, C, R>> getTransition(S nextState) {
        for (Transaction<S, E, C, ?> transaction : transactions) {
            if (transaction.getNextState().equals(nextState)) {
                return Optional.of((Transaction<S, E, C, R>) transaction);
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

        private final List<Transaction<S, E, C, ?>> transactions;
        private final S state;
        private Consumer<C> entryAction;
        private Consumer<C> exitAction;

        /**
         * Constructs a new StateBuilder object.
         *
         * @param state the name of the state
         */
        public StateBuilder(S state) {
            this.state = state;

            transactions = new ArrayList<>();
            entryAction = null;
            exitAction = null;

        }


        /**
         * Add a transaction associated with this state.
         *
         * @param transaction The transaction to add.
         * @return The StateBuilder instance.
         */
        public <T> StateBuilder<S, E, C> withTransaction(Transaction<S, E, C, T> transaction) {
            transactions.add(transaction);
            return this;
        }

        /**
         * Add a transaction associated with this state.
         *
         * @param transactionList List of transactions
         * @return The StateBuilder instance.
         */
        public <T> StateBuilder<S, E, C> withTransactions(List<Transaction<S, E, C, T>> transactionList) {
            transactions.addAll(transactionList);
            return this;
        }

        /**
         * Add an entry action associated with this state.
         *
         * @param action The entry action as a Consumer instance.
         * @return The StateBuilder instance.
         */
        public StateBuilder<S, E, C> withEntryAction(Consumer<C> action) {
            entryAction = action;
            return this;
        }

        /**
         * Add an exit action associated with this state.
         *
         * @param action The exit action as a Consumer instance.
         * @return The StateBuilder instance.
         */
        public StateBuilder<S, E, C> withExitAction(Consumer<C> action) {
            exitAction = action;
            return this;
        }

        /**
         * Finalize the state definition and return the parent builder.
         *
         * @return The parent builder instance.
         */
        public FsmState<S, E, C> build() {
            return new FsmState<>(state, transactions, entryAction, exitAction);
        }
    }
}
