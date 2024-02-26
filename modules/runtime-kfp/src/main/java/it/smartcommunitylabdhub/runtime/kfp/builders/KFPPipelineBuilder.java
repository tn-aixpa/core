package it.smartcommunitylabdhub.runtime.kfp.builders;

import it.smartcommunitylabdhub.commons.infrastructure.Builder;
import it.smartcommunitylabdhub.runtime.kfp.specs.function.FunctionKFPSpec;
import it.smartcommunitylabdhub.runtime.kfp.specs.run.RunKFPSpec;
import it.smartcommunitylabdhub.runtime.kfp.specs.task.TaskPipelineSpec;

import java.util.Optional;

/**
 * KFPPipelineBuilder
 * <p>
 * You can use this as a simple class or as a registered bean. If you want to retrieve this as bean from BuilderFactory
 * you have to register it using the following annotation:
 *
 * @BuilderComponent(runtime = "kfp", task = "pipeline")
 */

public class KFPPipelineBuilder implements Builder<FunctionKFPSpec, TaskPipelineSpec, RunKFPSpec> {

    @Override
    public RunKFPSpec build(FunctionKFPSpec funSpec, TaskPipelineSpec taskSpec, RunKFPSpec runSpec) {
        RunKFPSpec containerSpec = new RunKFPSpec(runSpec.toMap());
        containerSpec.setTaskPipelineSpec(taskSpec);
        containerSpec.setFunctionSpec(funSpec);

        //let run override k8s specs
        Optional
            .ofNullable(runSpec.getTaskPipelineSpec())
            .ifPresent(k8sSpec -> containerSpec.getTaskPipelineSpec().configure(k8sSpec.toMap()));

        return containerSpec;
    }
}
