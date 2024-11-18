package it.smartcommunitylabdhub.runtime.kfp.specs;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.jackson.annotations.JsonSchemaIgnore;
import it.smartcommunitylabdhub.commons.models.entities.EntityName;
import it.smartcommunitylabdhub.commons.models.run.RunBaseSpec;
import it.smartcommunitylabdhub.runtime.kfp.KFPRuntime;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SpecType(runtime = KFPRuntime.RUNTIME, kind = KFPRunSpec.KIND, entity = EntityName.RUN)
public class KFPRunSpec extends RunBaseSpec {

    public static final String KIND = KFPRuntime.RUNTIME + "+run";

    private Map<String, String> inputs = new HashMap<>();

    private Map<String, String> outputs = new HashMap<>();

    private Map<String, Serializable> parameters = new HashMap<>();

    // @JsonProperty("workflow_spec")
    @JsonSchemaIgnore
    @JsonUnwrapped
    private KFPWorkflowSpec workflowSpec;

    // @JsonProperty("pipeline_spec")
    @JsonUnwrapped
    private KFPPipelineTaskSpec taskPipelineSpec;

    @JsonUnwrapped
    private KFPBuildTaskSpec taskBuildSpec;

    public KFPRunSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        KFPRunSpec spec = mapper.convertValue(data, KFPRunSpec.class);
        this.inputs = spec.getInputs();
        this.outputs = spec.getOutputs();
        this.parameters = spec.getParameters();

        this.taskPipelineSpec = spec.getTaskPipelineSpec();
        this.taskBuildSpec = spec.getTaskBuildSpec();
        this.workflowSpec = spec.getWorkflowSpec();
    }

    public void setWorkflowSpec(KFPWorkflowSpec workflowSpec) {
        this.workflowSpec = workflowSpec;
    }

    public void setTaskPipelineSpec(KFPPipelineTaskSpec taskPipelineSpec) {
        this.taskPipelineSpec = taskPipelineSpec;
    }

    public void setTaskBuildSpec(KFPBuildTaskSpec taskBuildSpec) {
        this.taskBuildSpec = taskBuildSpec;
    }
}
