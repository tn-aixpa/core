package it.smartcommunitylabdhub.runtime.python.specs.run;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.runtime.python.PythonRuntime;
import it.smartcommunitylabdhub.runtime.python.specs.function.PythonFunctionSpec;
import it.smartcommunitylabdhub.runtime.python.specs.task.PythonJobTaskSpec;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SpecType(runtime = PythonRuntime.RUNTIME, kind = PythonRunSpec.KIND, entity = EntityName.RUN)
public class PythonRunSpec extends RunBaseSpec {

    public static final String KIND = PythonRuntime.RUNTIME + "+run";

    @JsonUnwrapped
    private PythonJobTaskSpec taskJobSpec;

    @JsonUnwrapped
    private PythonFunctionSpec functionSpec;

    private Map<String, Serializable> inputs = new HashMap<>();

    private Map<String, Serializable> outputs = new HashMap<>();

    private Map<String, Serializable> parameters = new HashMap<>();

    public PythonRunSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        PythonRunSpec spec = mapper.convertValue(data, PythonRunSpec.class);

        this.taskJobSpec = spec.getTaskJobSpec();
        this.functionSpec = spec.getFunctionSpec();

        this.inputs = spec.getInputs();
        this.outputs = spec.getOutputs();
        this.parameters = spec.getParameters();
    }

    public void setTaskJobSpec(PythonJobTaskSpec taskJobSpec) {
        this.taskJobSpec = taskJobSpec;
    }

    public void setFunctionSpec(PythonFunctionSpec functionSpec) {
        this.functionSpec = functionSpec;
    }
}
