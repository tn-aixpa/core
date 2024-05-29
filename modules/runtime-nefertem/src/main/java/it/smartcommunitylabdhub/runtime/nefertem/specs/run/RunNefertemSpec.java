package it.smartcommunitylabdhub.runtime.nefertem.specs.run;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.runtime.nefertem.NefertemRuntime;
import it.smartcommunitylabdhub.runtime.nefertem.specs.function.FunctionNefertemSpec;
import it.smartcommunitylabdhub.runtime.nefertem.specs.task.TaskInferSpec;
import it.smartcommunitylabdhub.runtime.nefertem.specs.task.TaskMetricSpec;
import it.smartcommunitylabdhub.runtime.nefertem.specs.task.TaskProfileSpec;
import it.smartcommunitylabdhub.runtime.nefertem.specs.task.TaskValidateSpec;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SpecType(runtime = NefertemRuntime.RUNTIME, kind = RunNefertemSpec.KIND, entity = EntityName.RUN)
public class RunNefertemSpec extends RunBaseSpec {

    public static final String KIND = NefertemRuntime.RUNTIME + "+run";

    private Map<String, Serializable> inputs = new HashMap<>();

    private Map<String, Serializable> outputs = new HashMap<>();

    private Map<String, Serializable> parameters = new HashMap<>();

    // @JsonProperty("infer_spec")
    @JsonUnwrapped
    private TaskInferSpec taskInferSpec;

    // @JsonProperty("metric_spec")
    @JsonUnwrapped
    private TaskMetricSpec taskMetricSpec;

    // @JsonProperty("profile_spec")
    @JsonUnwrapped
    private TaskProfileSpec taskProfileSpec;

    // @JsonProperty("validate_spec")
    @JsonUnwrapped
    private TaskValidateSpec taskValidateSpec;

    // @JsonProperty("function_spec")
    @JsonUnwrapped
    private FunctionNefertemSpec funcSpec;

    public RunNefertemSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        RunNefertemSpec spec = mapper.convertValue(data, RunNefertemSpec.class);
        this.inputs = spec.getInputs();
        this.outputs = spec.getOutputs();
        this.parameters = spec.getParameters();

        this.taskInferSpec = spec.getTaskInferSpec();
        this.taskMetricSpec = spec.getTaskMetricSpec();
        this.taskProfileSpec = spec.getTaskProfileSpec();
        this.taskValidateSpec = spec.getTaskValidateSpec();
        this.funcSpec = spec.getFuncSpec();
    }

    public void setTaskInferSpec(TaskInferSpec taskInferSpec) {
        this.taskInferSpec = taskInferSpec;
    }

    public void setTaskMetricSpec(TaskMetricSpec taskMetricSpec) {
        this.taskMetricSpec = taskMetricSpec;
    }

    public void setTaskProfileSpec(TaskProfileSpec taskProfileSpec) {
        this.taskProfileSpec = taskProfileSpec;
    }

    public void setTaskValidateSpec(TaskValidateSpec taskValidateSpec) {
        this.taskValidateSpec = taskValidateSpec;
    }

    public void setFuncSpec(FunctionNefertemSpec funcSpec) {
        this.funcSpec = funcSpec;
    }
}
