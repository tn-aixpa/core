package it.smartcommunitylabdhub.runtime.kfp.specs;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
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
    @JsonUnwrapped
    private KFPWorkflowSpec workflowSpec;

    // @JsonProperty("pipeline_spec")
    @JsonUnwrapped
    private KFPPipelineTaskSpec taskSpec;

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

        this.taskSpec = spec.getTaskSpec();
        this.workflowSpec = spec.getWorkflowSpec();
    }

    public void setWorkflowSpec(KFPWorkflowSpec workflowSpec) {
        this.workflowSpec = workflowSpec;
    }

    public void setTaskSpec(KFPPipelineTaskSpec taskSpec) {
        this.taskSpec = taskSpec;
    }
}
