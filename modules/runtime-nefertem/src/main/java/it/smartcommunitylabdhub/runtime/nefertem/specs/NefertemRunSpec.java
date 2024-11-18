package it.smartcommunitylabdhub.runtime.nefertem.specs;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.jackson.annotations.JsonSchemaIgnore;
import it.smartcommunitylabdhub.commons.models.entities.EntityName;
import it.smartcommunitylabdhub.commons.models.run.RunBaseSpec;
import it.smartcommunitylabdhub.runtime.nefertem.NefertemRuntime;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SpecType(runtime = NefertemRuntime.RUNTIME, kind = NefertemRunSpec.KIND, entity = EntityName.RUN)
public class NefertemRunSpec extends RunBaseSpec {

    public static final String KIND = NefertemRuntime.RUNTIME + "+run";

    private Map<String, String> inputs = new HashMap<>();

    private Map<String, String> outputs = new HashMap<>();

    private Map<String, Serializable> parameters = new HashMap<>();

    @JsonUnwrapped
    private NefertemInferTaskSpec taskInferSpec;

    @JsonUnwrapped
    private NefertemMetricTaskSpec taskMetricSpec;

    @JsonUnwrapped
    private NefertemProfileTaskSpec taskProfileSpec;

    @JsonUnwrapped
    private NefertemValidateTaskSpec taskValidateSpec;

    @JsonSchemaIgnore
    @JsonUnwrapped
    private NefertemFunctionSpec functionSpec;

    public NefertemRunSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        NefertemRunSpec spec = mapper.convertValue(data, NefertemRunSpec.class);
        this.inputs = spec.getInputs();
        this.outputs = spec.getOutputs();
        this.parameters = spec.getParameters();

        this.taskInferSpec = spec.getTaskInferSpec();
        this.taskMetricSpec = spec.getTaskMetricSpec();
        this.taskProfileSpec = spec.getTaskProfileSpec();
        this.taskValidateSpec = spec.getTaskValidateSpec();
        this.functionSpec = spec.getFunctionSpec();
    }

    public void setTaskInferSpec(NefertemInferTaskSpec taskInferSpec) {
        this.taskInferSpec = taskInferSpec;
    }

    public void setTaskMetricSpec(NefertemMetricTaskSpec taskMetricSpec) {
        this.taskMetricSpec = taskMetricSpec;
    }

    public void setTaskProfileSpec(NefertemProfileTaskSpec taskProfileSpec) {
        this.taskProfileSpec = taskProfileSpec;
    }

    public void setTaskValidateSpec(NefertemValidateTaskSpec taskValidateSpec) {
        this.taskValidateSpec = taskValidateSpec;
    }

    public void setFunctionSpec(NefertemFunctionSpec funcSpec) {
        this.functionSpec = funcSpec;
    }
}
