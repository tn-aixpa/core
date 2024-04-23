package it.smartcommunitylabdhub.runtime.mlrun.specs.run;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.runtime.mlrun.MlrunRuntime;
import it.smartcommunitylabdhub.runtime.mlrun.specs.function.FunctionMlrunSpec;
import it.smartcommunitylabdhub.runtime.mlrun.specs.task.TaskMlrunBuildSpec;
import it.smartcommunitylabdhub.runtime.mlrun.specs.task.TaskMlrunJobSpec;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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

    private List<Map.Entry<String, Serializable>> inputs = new LinkedList<>();

    private List<Map.Entry<String, Serializable>> outputs = new LinkedList<>();

    private Map<String, Serializable> parameters = new HashMap<>();

    @JsonProperty("job_spec")
    private TaskMlrunJobSpec jobSpec;
    @JsonProperty("build_spec")
    private TaskMlrunBuildSpec buildSpec;

    @JsonProperty("function_spec")
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
}
