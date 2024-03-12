/**
 * StateMachine.java
 * <p>
 * This class represents a State Machine that handles the flow of states and transitions based on
 * events and guards. It allows the definition of states and transitions along with their associated
 * actions and guards.
 *
 * @param <S> The type of the states.
 * @param <E> The type of the events.
 * @param <C> The type of the context.
 */

package it.smartcommunitylabdhub.fsm;

import it.smartcommunitylabdhub.fsm.exceptions.InvalidTransactionException;
import jakarta.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Fsm<S, E, C> {

    private final ReentrantLock stateLock = new ReentrantLock();

    @Getter
    @Setter
    private String uuid;

    @Getter
    private S currentState;

    private Map<S, FsmState<S, E, C>> states;

    @Getter
    @Setter
    private ConcurrentHashMap<E, BiConsumer<C, C>> eventListeners;

    @Getter
    @Setter
    private BiConsumer<S, C> stateChangeListener;

    private Context<C> context;

    /**
     * Default constructor to create an empty StateMachine.
     */
    public Fsm() {}

    /**
     * Constructor to create a StateMachine with the initial state and context.
     *
     * @param initialState   The initial state of the StateMachine.
     * @param initialContext The initial context for the StateMachine.
     */
    public Fsm(S initialState, Context<C> initialContext) {
        this.uuid = UUID.randomUUID().toString();
        this.currentState = initialState;
        this.context = initialContext;
        this.states = new ConcurrentHashMap<>();
        this.eventListeners = new ConcurrentHashMap<>();
    }

    /**
     * Static builder method to create a new StateMachine.
     *
     * @param initialState   The initial state of the StateMachine.
     * @param initialContext The initial context for the StateMachine.
     * @return A new Builder instance to configure and build the StateMachine.
     */
    public static <S, E, C> Builder<S, E, C> builder(S initialState, C initialContext) {
        return new Builder<>(initialState, initialContext);
    }

    /**
     * Transition the state machine to a target state based on the provided input. This method executes
     * the necessary actions and guards associated with the transition from the current state to the
     * target state. It follows the path of valid transitions from the current state to the target state
     * and performs the following steps:
     * 1. Checks if a valid path exists from the current state to the target state.
     * 2. Executes the exit action of each intermediate state along the path.
     * 3. Executes the entry action of the target state.
     * 4. Applies the internal logic associated with the target state.
     * <p>
     * If no valid path exists, the state machine transitions to the error state.
     *
     * @param targetState The state to transition to.
     * @param input       The input associated with the transition.
     * @param <R>         The type of the input.
     */
    public <R> Optional<R> goToState(S targetState, @Nullable C input) throws InvalidTransactionException {
        return acquireLock()
            .flatMap(lockAcquired -> {
                if (lockAcquired) {
                    try {
                        // Check if a valid path exists from the current state to the target state
                        List<S> path = findPath(currentState, targetState);
                        if (path.isEmpty()) {
                            // No valid path exists; transition to the error state
                            throw new InvalidTransactionException(currentState.toString(), targetState.toString());
                        }

                        // Follow the path for all element except the last transaction
                        // Execute ExitAction
                        // Verify the Guard of the transaction
                        // Execute Transaction, and it's internal logic
                        // Execute EnterAction
                        for (int i = 0; i < path.size() - 2; i++) {
                            execute(path.get(i), path.get(i + 1), input);
                        }

                        // Execute last transaction and collect result.
                        return execute(path.get(path.size() - 2), path.getLast(), input);
                    } finally {
                        stateLock.unlock();
                    }
                }
                return Optional.empty();
            });
    }

    /**
     * Add a single event listener to the map if not present
     *
     * @param event
     * @param listener
     */
    public void setEventListener(E event, BiConsumer<C, C> listener) {
        eventListeners.putIfAbsent(event, listener);
    }

    public FsmState<S, E, C> getState(S state) {
        return states.get(state);
    }

    /**
     * Find a path from the source state to the target state in the state machine.
     * <p>
     * This method initiates a depth-first search (DFS) to explore the state machine's transitions
     * and find a valid path from the source state to the target state.
     *
     * @param sourceState The starting state of the path.
     * @param targetState The state to reach.
     * @return A list of states representing a valid path from the source state to the target state,
     * or an empty list if no valid path is found.
     */
    private <R> List<S> findPath(S sourceState, S targetState) {
        Set<S> visited = new HashSet<>();
        LinkedList<S> path = new LinkedList<>();

        // Call the recursive DFS function to find the path
        if (this.<R>dfs(sourceState, targetState, visited, path)) {
            // If a valid path exists, return it
            return path;
        } else {
            // If no valid path exists, return an empty list
            return Collections.emptyList();
        }
    }

    private <R> Optional<R> execute(S stateInPath, S nextStateInPath, C input) {
        // Get state definition
        //S stateInPath = path.get(i);
        FsmState<S, E, C> stateDefinition = states.get(stateInPath);

        // execute exit action
        Optional
            .ofNullable(stateDefinition.getExitAction())
            .ifPresent(exitAction -> exitAction.accept(context.getValue()));

        // Get next state if exist and execute logic
        return Optional
            .ofNullable(nextStateInPath)
            .flatMap(nextState -> {
                // Retrieve the transition event dynamically
                Optional<Transaction<S, E, C, R>> transaction = stateDefinition.getTransition(nextState);

                // Check if a transition event exists and if guard condition is satisfied
                return transaction.map(transition -> {
                    if (transition.getGuard().test(context.getValue(), input)) {
                        // Notify event listeners for the transition event
                        notifyEventListeners(transition.getEvent(), input);

                        // Apply internal logic of the target state
                        Optional<R> result = transition
                            .getInternalLogic()
                            .flatMap(internalFunc -> applyInternalFunc(internalFunc, input));

                        // Update the current state and notify state change listener
                        currentState = nextState;

                        // Notify listener for state changed
                        notifyStateChangeListener(currentState);

                        // Retrieve the current state definition
                        FsmState<?, ?, C> currentStateDefinition = states.get(currentState);

                        // Execute entry action
                        Optional
                            .ofNullable(currentStateDefinition.getEntryAction())
                            .ifPresent(entryAction -> entryAction.accept(context.getValue()));

                        return result;
                    } else {
                        return Optional.<R>empty();
                        // Guard condition not satisfied, skip this transition
                    }
                });
            })
            .orElse(Optional.empty());
    }

    /**
     * Depth-First Search (DFS) function to find a path between two states in the state machine.
     * <p>
     * This function explores the state machine's transitions in a depth-first manner to find a path
     * from the current state to the target state.
     *
     * @param currentState The current state being explored.
     * @param targetState  The target state to reach.
     * @param visited      A set to keep track of visited states during the search.
     * @param path         A linked list to record the current path being explored.
     * @return True if a valid path is found from the current state to the target state, otherwise
     * false.
     */
    private <R> boolean dfs(S currentState, S targetState, Set<S> visited, LinkedList<S> path) {
        // Mark the current state as visited and add it to the path
        visited.add(currentState);
        path.addLast(currentState);

        // If the current state is the target state, a valid path is found
        if (currentState.equals(targetState)) {
            return true;
        }

        // Get the current state's definition
        FsmState<S, E, C> stateDefinition = states.get(currentState);

        if (stateDefinition != null) {
            // Iterate over the transitions from the current state
            for (Transaction<S, E, C, ?> transaction : stateDefinition.getTransactions()) {
                // Check if the next state in the transaction is unvisited
                if (!visited.contains(transaction.getNextState())) {
                    // Recursively search for a path from the next state to the target state
                    if (this.<R>dfs(transaction.getNextState(), targetState, visited, path)) {
                        return true; // A valid path is found
                    }
                }
            }
        }

        // If no valid path is found from the current state, backtrack
        path.removeLast();
        return false;
    }

    /**
     * Applies the internal logic associated with a state, allowing for customized handling of state
     * transitions and updates to the state machine's context.
     *
     * @param stateLogic The state logic implementation to apply.
     * @param <T>        The type of result returned by the state logic.
     * @return An optional result obtained from applying the internal logic, or empty if not
     * applicable.
     */
    private <T> Optional<T> applyInternalFunc(StateLogic<S, E, C, T> stateLogic, @Nullable C input) {
        return stateLogic.applyLogic(context.getValue(), input, this);
    }

    /**
     * Notifies the state change listener, if registered, about a state transition.
     *
     * @param newState The new state to which the state machine has transitioned.
     */
    private void notifyStateChangeListener(S newState) {
        if (stateChangeListener != null) {
            stateChangeListener.accept(newState, context.getValue());
        }
    }

    /**
     * Notifies event listeners, if registered, about an event associated with a state.
     *
     * @param eventName The event name that occurred.
     * @param input     The input associated with the event.
     */
    private <T> void notifyEventListeners(E eventName, @Nullable C input) {
        BiConsumer<C, C> listener = eventListeners.get(eventName);
        if (listener != null) {
            listener.accept(context.getValue(), input);
        }
    }

    /**
     * Attempt to acquire a lock with a timeout.
     *
     * @return An {@code Optional<Boolean>} representing the lock acquisition result. If the lock is
     * acquired successfully, it contains {@code true}; otherwise, it contains
     * {@code false}.
     */
    private Optional<Boolean> acquireLock() {
        try {
            boolean lockAcquired = stateLock.tryLock(10L, TimeUnit.MINUTES);
            return Optional.of(lockAcquired); // Return Optional with lock acquisition result.
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            return Optional.empty(); // Return Optional.empty() in case of an interruption or
            // exception.
        }
    }

    // Builder
    public static class Builder<S, E, C> {

        private final S currentState;

        @Getter
        private final Map<S, FsmState<S, E, C>> states;

        private final ConcurrentHashMap<E, BiConsumer<C, C>> eventListeners;
        private final Context<C> initialContext;
        private BiConsumer<S, C> stateChangeListener;

        public Builder(S initialState, C initialContext) {
            this.currentState = initialState;
            this.initialContext = new Context<>(initialContext);
            this.states = new ConcurrentHashMap<>();
            this.eventListeners = new ConcurrentHashMap<>();
        }

        /**
         * Adds a state and its definition to the builder's configuration.
         *
         * @param state The state to add.
         * @return This builder instance of the state, allowing for method chaining.
         */

        public Builder<S, E, C> withState(S state, FsmState<S, E, C> stateDefinition) {
            states.put(state, stateDefinition);
            return this;
        }

        /**
         * Adds an event listener to the builder's configuration.
         *
         * @param eventName The name of the event to listen for.
         * @param listener  The listener to handle the event.
         * @return This builder instance, allowing for method chaining.
         */
        public Builder<S, E, C> withEventListener(E eventName, BiConsumer<C, C> listener) {
            eventListeners.put(eventName, listener);
            return this;
        }

        /**
         * Sets the state change listener for the builder's configuration.
         *
         * @param listener The listener to be notified when the state changes.
         * @return This builder instance, allowing for method chaining.
         */
        public Builder<S, E, C> withStateChangeListener(BiConsumer<S, C> listener) {
            stateChangeListener = listener;
            return this;
        }

        public Fsm<S, E, C> build() {
            Fsm<S, E, C> stateMachine = new Fsm<>(currentState, initialContext);
            stateMachine.states = Collections.unmodifiableMap(states);
            stateMachine.eventListeners = eventListeners;
            stateMachine.stateChangeListener = stateChangeListener;

            return stateMachine;
        }
    }
}
