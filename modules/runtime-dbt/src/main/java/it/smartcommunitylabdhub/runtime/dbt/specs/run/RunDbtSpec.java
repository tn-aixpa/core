package it.smartcommunitylabdhub.runtime.dbt.specs.run;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.runtime.dbt.DbtRuntime;
import it.smartcommunitylabdhub.runtime.dbt.specs.function.FunctionDbtSpec;
import it.smartcommunitylabdhub.runtime.dbt.specs.task.TaskTransformSpec;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SpecType(runtime = DbtRuntime.RUNTIME, kind = RunDbtSpec.KIND, entity = EntityName.RUN)
public class RunDbtSpec extends RunBaseSpec {

    public static final String KIND = DbtRuntime.RUNTIME + "+run";

    private Map<String, Serializable> inputs = new HashMap<>();

    private Map<String, Serializable> outputs = new HashMap<>();

    private Map<String, Serializable> parameters = new HashMap<>();

    // @JsonProperty("transform_spec")
    @JsonUnwrapped
    private TaskTransformSpec taskSpec;

    // @JsonProperty("function_spec")
    @JsonUnwrapped
    private FunctionDbtSpec funcSpec;

    public RunDbtSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        RunDbtSpec spec = mapper.convertValue(data, RunDbtSpec.class);
        this.inputs = spec.getInputs();
        this.outputs = spec.getOutputs();
        this.parameters = spec.getParameters();

        this.taskSpec = spec.getTaskSpec();
        this.funcSpec = spec.getFuncSpec();
    }

    public void setTaskSpec(TaskTransformSpec taskSpec) {
        this.taskSpec = taskSpec;
    }

    public void setFuncSpec(FunctionDbtSpec funcSpec) {
        this.funcSpec = funcSpec;
    }
}
