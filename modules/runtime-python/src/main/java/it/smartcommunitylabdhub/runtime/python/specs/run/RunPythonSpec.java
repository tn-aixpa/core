package it.smartcommunitylabdhub.runtime.python.specs.run;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.runtime.python.PythonRuntime;
import it.smartcommunitylabdhub.runtime.python.specs.function.FunctionPythonSpec;
import it.smartcommunitylabdhub.runtime.python.specs.task.TaskBuildSpec;
import it.smartcommunitylabdhub.runtime.python.specs.task.TaskJobSpec;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@SpecType(runtime = PythonRuntime.RUNTIME, kind = RunPythonSpec.KIND, entity = EntityName.RUN)
public class RunPythonSpec extends RunBaseSpec {

    public static final String KIND = PythonRuntime.RUNTIME + "+run";

    @JsonProperty("job_spec")
    private TaskJobSpec taskJobSpec;
    

    @JsonProperty("build_spec")
    private TaskBuildSpec taskBuildSpec;

    @JsonProperty("function_spec")
    private FunctionPythonSpec functionSpec;

    public RunPythonSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        RunPythonSpec spec = mapper.convertValue(data, RunPythonSpec.class);

        this.taskJobSpec = spec.getTaskJobSpec();
        this.functionSpec = spec.getFunctionSpec();
        this.taskBuildSpec = spec.getTaskBuildSpec();
    }
}
