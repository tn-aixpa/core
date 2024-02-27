package it.smartcommunitylabdhub.runtime.mlrun.specs.run;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.runtime.mlrun.MlrunRuntime;
import it.smartcommunitylabdhub.runtime.mlrun.specs.function.FunctionMlrunSpec;
import it.smartcommunitylabdhub.runtime.mlrun.specs.task.TaskMlrunSpec;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SpecType(runtime = MlrunRuntime.RUNTIME, kind = "mlrun+run", entity = EntityName.RUN)
public class RunMlrunSpec extends RunBaseSpec {

    @JsonProperty("mlrun_spec")
    private TaskMlrunSpec taskSpec;

    @JsonProperty("function_spec")
    private FunctionMlrunSpec funcSpec;

    public RunMlrunSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        RunMlrunSpec runMlrunSpec = mapper.convertValue(data, RunMlrunSpec.class);

        this.setTaskSpec(runMlrunSpec.getTaskSpec());
        this.setFuncSpec(runMlrunSpec.getFuncSpec());
    }
}
