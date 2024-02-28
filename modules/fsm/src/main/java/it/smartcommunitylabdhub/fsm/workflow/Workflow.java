/**
 * Workflow.java
 * <p>
 * This class represents a workflow that executes a series of steps sequentially.
 * Each step is represented as a Function, and the output of each step is passed as input to the next step.
 * It provides both synchronous and asynchronous execution of the workflow.
 */

package it.smartcommunitylabdhub.fsm.workflow;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
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
     * Asynchronously executes each step of the workflow in parallel without waiting for the completion
     * of the previous step. This method is suitable when you don't need to use the result of each step
     * and want to execute all steps in parallel.
     *
     * @param input The input for the workflow execution.
     * @param <I>   The type of the input.
     * @param <O>   The type of the output. Since this method doesn't return the result of each step,
     *              the output type is usually set to Void.
     * @return A CompletableFuture representing the completion of the entire workflow. The CompletableFuture
     * completes when all steps of the workflow have finished execution.
     */
    @SuppressWarnings("unchecked")
    public <I, O> CompletableFuture<O> executeAsync(I input) {
        // List to hold CompletableFuture objects for each step
        List<CompletableFuture<Object>> stepFutures = new ArrayList<>();

        // Execute each step asynchronously and collect the CompletableFuture objects
        for (Function<Object, Object> step : (List<Function<Object, Object>>) (List<?>) steps) {
            // Execute each step asynchronously and add the CompletableFuture to the list
            stepFutures.add(CompletableFuture.supplyAsync(() -> step.apply(input)));
        }

        // Combine all step results
        CompletableFuture<Void> allStepsFuture = CompletableFuture.allOf(stepFutures.toArray(new CompletableFuture[0]));

        // Combine the results of all steps into a single CompletableFuture
        return allStepsFuture.thenApply(voidResult -> {
            // Combine the results of all steps into a single result
            List<Object> results = new ArrayList<>();
            for (CompletableFuture<Object> stepFuture : stepFutures) {
                try {
                    // Wait for each step to complete (we don't need the result)
                    stepFuture.join();
                    // Add a placeholder for each step result
                    results.add(null);
                } catch (CompletionException ex) {
                    // Handle any exceptions that occurred during step execution
                    // You might want to handle or log these exceptions appropriately
                }
            }
            // Return null as the combined result (since we don't need the result of each step)
            return (O) results;
        });
    }
}
