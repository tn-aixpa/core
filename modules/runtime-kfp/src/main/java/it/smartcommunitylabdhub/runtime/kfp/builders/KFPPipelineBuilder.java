package it.smartcommunitylabdhub.runtime.kfp.builders;

import it.smartcommunitylabdhub.commons.infrastructure.Builder;
import it.smartcommunitylabdhub.runtime.kfp.specs.run.RunKFPSpec;
import it.smartcommunitylabdhub.runtime.kfp.specs.task.TaskPipelineSpec;
import it.smartcommunitylabdhub.runtime.kfp.specs.workflow.WorkflowKFPSpec;

import java.util.Optional;

/**
 * KFPPipelineBuilder
 * <p>
 * You can use this as a simple class or as a registered bean. If you want to retrieve this as bean from BuilderFactory
 * you have to register it using the following annotation:
 *
 * @BuilderComponent(runtime = "kfp", task = "pipeline")
 */

public class KFPPipelineBuilder implements Builder<WorkflowKFPSpec, TaskPipelineSpec, RunKFPSpec> {

    @Override
    public RunKFPSpec build(WorkflowKFPSpec wfSpec, TaskPipelineSpec taskSpec, RunKFPSpec runSpec) {
        RunKFPSpec spec = new RunKFPSpec(runSpec.toMap());
        spec.setTaskSpec(taskSpec);
        spec.setWorkflowSpec(wfSpec);

        //let run override k8s specs
        Optional.ofNullable(runSpec.getTaskSpec()).ifPresent(k8sSpec -> spec.getTaskSpec().configure(k8sSpec.toMap()));

        return spec;
    }
}
