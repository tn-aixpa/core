package it.smartcommunitylabdhub.runtime.dbt.specs.run;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.runtime.dbt.DbtRuntime;
import it.smartcommunitylabdhub.runtime.dbt.specs.function.DbtFunctionSpec;
import it.smartcommunitylabdhub.runtime.dbt.specs.task.DbtTransformSpec;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SpecType(runtime = DbtRuntime.RUNTIME, kind = DbtRunSpec.KIND, entity = EntityName.RUN)
public class DbtRunSpec extends RunBaseSpec {

    public static final String KIND = DbtRuntime.RUNTIME + "+run";

    private Map<String, String> inputs = new HashMap<>();

    private Map<String, String> outputs = new HashMap<>();

    private Map<String, Serializable> parameters = new HashMap<>();

    @JsonUnwrapped
    private DbtTransformSpec taskSpec;

    @JsonUnwrapped
    private DbtFunctionSpec functionSpec;

    public DbtRunSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        DbtRunSpec spec = mapper.convertValue(data, DbtRunSpec.class);
        this.inputs = spec.getInputs();
        this.outputs = spec.getOutputs();
        this.parameters = spec.getParameters();

        this.taskSpec = spec.getTaskSpec();
        this.functionSpec = spec.getFunctionSpec();
    }

    public void setTaskSpec(DbtTransformSpec taskSpec) {
        this.taskSpec = taskSpec;
    }

    public void setFunctionSpec(DbtFunctionSpec funcSpec) {
        this.functionSpec = funcSpec;
    }
}
