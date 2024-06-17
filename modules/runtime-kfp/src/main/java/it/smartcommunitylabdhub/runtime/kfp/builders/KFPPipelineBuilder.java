package it.smartcommunitylabdhub.runtime.kfp.builders;

import it.smartcommunitylabdhub.commons.infrastructure.Builder;
import it.smartcommunitylabdhub.runtime.kfp.specs.run.RunKFPSpec;
import it.smartcommunitylabdhub.runtime.kfp.specs.task.TaskPipelineSpec;
import it.smartcommunitylabdhub.runtime.kfp.specs.workflow.WorkflowKFPSpec;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
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

        Map<String, Serializable> map = new HashMap<>();
        map.putAll(runSpec.toMap());
        map.putAll(wfSpec.toMap());
        taskSpec.toMap().forEach(map::putIfAbsent);

        RunKFPSpec runKFPSpec = new RunKFPSpec(map);
        runKFPSpec.setWorkflowSpec(wfSpec);

        return runKFPSpec;
    }
}
