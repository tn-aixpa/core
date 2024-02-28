/**
 * WorkflowFactory.java
 * <p>
 * This class is a factory to build workflows by adding individual steps.
 * The steps are represented as Functions that are executed sequentially.
 * <p>
 * The idea is that each kind function, artifact, dataitem, workflow has
 * their workflow to speak with Some external services.
 */

package it.smartcommunitylabdhub.fsm.workflow;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class WorkflowFactory {

    private final List<Function<?, ?>> steps;

    private WorkflowFactory() {
        this.steps = new ArrayList<>();
    }

    public static WorkflowFactory builder() {
        return new WorkflowFactory();
    }

    /**
     * Add a step to the workflow.
     *
     * @param step The step represented as a Function.
     * @param <I>  The input type of the step.
     * @param <O>  The output type of the step.
     * @return The WorkflowFactory instance with the added step.
     */
    public <I, O> WorkflowFactory step(Function<I, O> step) {
        steps.add(step);
        return this;
    }

    /**
     * Add a step to the workflow with a fixed argument.
     *
     * @param step     The step represented as a Function.
     * @param argument The fixed argument for the step.
     * @param <I>      The input type of the step.
     * @param <O>      The output type of the step.
     * @return The WorkflowFactory instance with the added step.
     */
    public <I, O> WorkflowFactory step(Function<I, O> step, I argument) {
        steps.add(input -> step.apply(argument));
        return this;
    }

    /**
     * Add a step to the workflow with multiple arguments.
     *
     * @param step     The step represented as a Function.
     * @param argument The array of arguments for the step.
     * @param <I>      The input type of the step.
     * @param <O>      The output type of the step.
     * @return The WorkflowFactory instance with the added step.
     */
    @SuppressWarnings("unchecked")
    public <I, O> WorkflowFactory step(Function<I[], O> step, I... argument) {
        steps.add(input -> step.apply(argument));
        return this;
    }

    /**
     * Add a conditional step to the workflow.
     *
     * @param condition The condition represented as a Function.
     * @param step      The step represented as a Function.
     * @param <I>       The input type of the step.
     * @param <O>       The output type of the step.
     * @return The WorkflowFactory instance with the added conditional step.
     */
    @SuppressWarnings("unchecked")
    public <I, O> WorkflowFactory conditionalStep(Function<I, Boolean> condition, Function<I, O> step) {
        steps.add(input -> {
            if (condition.apply((I) input)) {
                return step.apply((I) input);
            } else {
                return input; // Skip the step
            }
        });
        return this;
    }

    /**
     * Build the workflow using the added steps.
     *
     * @return The constructed Workflow instance.
     */
    public Workflow build() {
        return new Workflow(steps);
    }
}
