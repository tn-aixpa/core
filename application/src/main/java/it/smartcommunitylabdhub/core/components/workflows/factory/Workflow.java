
/**
 * Workflow.java
 *
 * This class represents a workflow that executes a series of steps sequentially.
 * Each step is represented as a Function, and the output of each step is passed as input to the next step.
 * It provides both synchronous and asynchronous execution of the workflow.
 */

package it.smartcommunitylabdhub.core.components.workflows.factory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class Workflow {
    private final List<Function<?, ?>> steps;

    public Workflow(List<Function<?, ?>> steps) {
        this.steps = steps;
    }

    /**
     * Execute the workflow synchronously.
     *
     * @param input The initial input for the workflow.
     * @param <I>   The input type.
     * @param <O>   The output type.
     * @return The result of the workflow execution.
     */
    @SuppressWarnings("unchecked")
    public <I, O> O execute(I input) {
        Object result = input;
        for (Function<?, ?> step : steps) {
            result = ((Function<Object, Object>) step).apply(result);
        }
        return (O) result;
    }

    /**
     * Execute the workflow asynchronously.
     *
     * @param input The initial input for the workflow.
     * @param <I>   The input type.
     * @param <O>   The output type.
     * @return A CompletableFuture representing the result of the workflow
     *         execution.
     */
    @SuppressWarnings("unchecked")
    public <I, O> CompletableFuture<O> executeAsync(I input) {
        CompletableFuture<Object> future = CompletableFuture.completedFuture(input);
        for (Function<Object, Object> step : (List<Function<Object, Object>>) (List<?>) steps) {
            future = future.thenComposeAsync(result -> CompletableFuture.supplyAsync(() -> step.apply(result)));
        }
        return future.thenApply(result -> (O) result);
    }
}