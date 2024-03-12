package it.smartcommunitylabdhub.runtime.nefertem.specs.run;

import com.fasterxml.jackson.annotation.JsonProperty;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SpecType(runtime = NefertemRuntime.RUNTIME, kind = RunNefertemSpec.KIND, entity = EntityName.RUN)
public class RunNefertemSpec extends RunBaseSpec {

    public static final String KIND = NefertemRuntime.RUNTIME + "+run";

    private List<Map.Entry<String, Serializable>> inputs = new LinkedList<>();

    private List<Map.Entry<String, Serializable>> outputs = new LinkedList<>();

    private Map<String, Serializable> parameters = new HashMap<>();

    @JsonProperty("infer_spec")
    private TaskInferSpec taskInferSpec;

    @JsonProperty("metric_spec")
    private TaskMetricSpec taskMetricSpec;

    @JsonProperty("profile_spec")
    private TaskProfileSpec taskProfileSpec;

    @JsonProperty("validate_spec")
    private TaskValidateSpec taskValidateSpec;

    @JsonProperty("function_spec")
    private FunctionNefertemSpec funcSpec;

    public RunNefertemSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        RunNefertemSpec spec = mapper.convertValue(data, RunNefertemSpec.class);
        this.setInputs(spec.getInputs());
        this.setOutputs(spec.getOutputs());
        this.setParameters(spec.getParameters());

        this.setTaskInferSpec(spec.getTaskInferSpec());
        this.setTaskMetricSpec(spec.getTaskMetricSpec());
        this.setTaskProfileSpec(spec.getTaskProfileSpec());
        this.setTaskValidateSpec(spec.getTaskValidateSpec());
        this.setFuncSpec(spec.getFuncSpec());
    }
}
