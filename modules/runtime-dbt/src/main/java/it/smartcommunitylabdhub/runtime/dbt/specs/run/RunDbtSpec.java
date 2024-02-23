package it.smartcommunitylabdhub.runtime.dbt.specs.run;

import com.fasterxml.jackson.annotation.JsonProperty;
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
@SpecType(runtime = DbtRuntime.RUNTIME, kind = "dbt+run", entity = EntityName.RUN)
public class RunDbtSpec extends RunBaseSpec {

    private Map<String, Object> inputs = new HashMap<>();

    private Map<String, Object> outputs = new HashMap<>();

    private Map<String, Object> parameters = new HashMap<>();

    @JsonProperty("transform_spec")
    private TaskTransformSpec taskSpec;

    @JsonProperty("function_spec")
    private FunctionDbtSpec funcSpec;

    public RunDbtSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        RunDbtSpec spec = mapper.convertValue(data, RunDbtSpec.class);
        this.setInputs(spec.getInputs());
        this.setOutputs(spec.getOutputs());
        this.setParameters(spec.getParameters());

        this.setTaskSpec(spec.getTaskSpec());
        this.setFuncSpec(spec.getFuncSpec());
    }
}
