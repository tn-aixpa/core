package it.smartcommunitylabdhub.runtime.mlrun.specs.run;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.runtime.mlrun.MlrunRuntime;
import it.smartcommunitylabdhub.runtime.mlrun.specs.function.FunctionMlrunSpec;
import it.smartcommunitylabdhub.runtime.mlrun.specs.task.TaskMlrunBuildSpec;
import it.smartcommunitylabdhub.runtime.mlrun.specs.task.TaskMlrunJobSpec;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SpecType(runtime = MlrunRuntime.RUNTIME, kind = RunMlrunSpec.KIND, entity = EntityName.RUN)
public class RunMlrunSpec extends RunBaseSpec {

    public static final String KIND = MlrunRuntime.RUNTIME + "+run";

    private Map<String, Serializable> inputs = new HashMap<>();

    private Map<String, Serializable> outputs = new HashMap<>();

    private Map<String, Serializable> parameters = new HashMap<>();

    // @JsonProperty("job_spec")
    @JsonUnwrapped
    private TaskMlrunJobSpec jobSpec;

    // @JsonProperty("build_spec")
    @JsonUnwrapped
    private TaskMlrunBuildSpec buildSpec;

    // @JsonProperty("function_spec")
    @JsonUnwrapped
    private FunctionMlrunSpec funcSpec;

    public RunMlrunSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        RunMlrunSpec spec = mapper.convertValue(data, RunMlrunSpec.class);
        this.inputs = spec.getInputs();
        this.outputs = spec.getOutputs();
        this.parameters = spec.getParameters();

        this.jobSpec = spec.getJobSpec();
        this.buildSpec = spec.getBuildSpec();
        this.funcSpec = spec.getFuncSpec();
    }

    public void setJobSpec(TaskMlrunJobSpec jobSpec) {
        this.jobSpec = jobSpec;
    }

    public void setBuildSpec(TaskMlrunBuildSpec buildSpec) {
        this.buildSpec = buildSpec;
    }

    public void setFuncSpec(FunctionMlrunSpec funcSpec) {
        this.funcSpec = funcSpec;
    }
}
