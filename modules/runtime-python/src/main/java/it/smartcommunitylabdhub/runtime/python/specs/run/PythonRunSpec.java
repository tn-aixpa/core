package it.smartcommunitylabdhub.runtime.python.specs.run;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.runtime.python.PythonRuntime;
import it.smartcommunitylabdhub.runtime.python.specs.function.PythonFunctionSpec;
import it.smartcommunitylabdhub.runtime.python.specs.task.PythonBuildTaskSpec;
import it.smartcommunitylabdhub.runtime.python.specs.task.PythonJobTaskSpec;
import it.smartcommunitylabdhub.runtime.python.specs.task.PythonServeTaskSpec;
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
    private PythonServeTaskSpec taskServeSpec;

    @JsonUnwrapped
    private PythonBuildTaskSpec taskBuildSpec;

    @JsonUnwrapped
    private PythonFunctionSpec functionSpec;

    private Map<String, String> inputs = new HashMap<>();

    private Map<String, String> outputs = new HashMap<>();

    private Map<String, Serializable> parameters = new HashMap<>();

    public PythonRunSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        PythonRunSpec spec = mapper.convertValue(data, PythonRunSpec.class);

        this.taskJobSpec = spec.getTaskJobSpec();
        this.taskServeSpec = spec.getTaskServeSpec();
        this.functionSpec = spec.getFunctionSpec();
        this.taskBuildSpec = spec.getTaskBuildSpec();

        this.inputs = spec.getInputs();
        this.outputs = spec.getOutputs();
        this.parameters = spec.getParameters();
    }

    public void setTaskJobSpec(PythonJobTaskSpec taskJobSpec) {
        this.taskJobSpec = taskJobSpec;
    }

    public void setTaskServeSpec(PythonServeTaskSpec taskServeSpec) {
        this.taskServeSpec = taskServeSpec;
    }

    public void setTaskBuildSpec(PythonBuildTaskSpec buildTaskSpec) {
        this.taskBuildSpec = buildTaskSpec;
    }

    public void setFunctionSpec(PythonFunctionSpec functionSpec) {
        this.functionSpec = functionSpec;
    }
}
