package it.smartcommunitylabdhub.runtime.kfp.specs.run;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.runtime.kfp.KFPRuntime;
import it.smartcommunitylabdhub.runtime.kfp.specs.task.TaskPipelineSpec;
import it.smartcommunitylabdhub.runtime.kfp.specs.workflow.WorkflowKFPSpec;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SpecType(runtime = KFPRuntime.RUNTIME, kind = RunKFPSpec.KIND, entity = EntityName.RUN)
public class RunKFPSpec extends RunBaseSpec {

    public static final String KIND = KFPRuntime.RUNTIME + "+run";

    private Map<String, String> inputs = new HashMap<>();

    private Map<String, String> outputs = new HashMap<>();

    private Map<String, Serializable> parameters = new HashMap<>();

    // @JsonProperty("workflow_spec")
    @JsonUnwrapped
    private WorkflowKFPSpec workflowSpec;

    // @JsonProperty("pipeline_spec")
    @JsonUnwrapped
    private TaskPipelineSpec taskSpec;

    public RunKFPSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        RunKFPSpec spec = mapper.convertValue(data, RunKFPSpec.class);
        this.inputs = spec.getInputs();
        this.outputs = spec.getOutputs();
        this.parameters = spec.getParameters();

        this.taskSpec = spec.getTaskSpec();
        this.workflowSpec = spec.getWorkflowSpec();
    }

    public void setWorkflowSpec(WorkflowKFPSpec workflowSpec) {
        this.workflowSpec = workflowSpec;
    }

    public void setTaskSpec(TaskPipelineSpec taskSpec) {
        this.taskSpec = taskSpec;
    }
}
