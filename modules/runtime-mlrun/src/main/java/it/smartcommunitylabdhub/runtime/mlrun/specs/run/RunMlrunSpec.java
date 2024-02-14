package it.smartcommunitylabdhub.runtime.mlrun.specs.run;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.jackson.JacksonMapper;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.runtime.mlrun.MlrunRuntime;
import it.smartcommunitylabdhub.runtime.mlrun.specs.function.FunctionMlrunSpec;
import it.smartcommunitylabdhub.runtime.mlrun.specs.task.TaskMlrunSpec;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SpecType(runtime = MlrunRuntime.RUNTIME, kind = "mlrun+run", entity = EntityName.RUN)
public class RunMlrunSpec extends RunBaseSpec {

    @JsonProperty("mlrun_spec")
    private TaskMlrunSpec taskSpec;

    @JsonProperty("function_spec")
    private FunctionMlrunSpec funcSpec;

    @Override
    public void configure(Map<String, Object> data) {
        RunMlrunSpec runMlrunSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(data, RunMlrunSpec.class);

        this.setTaskSpec(runMlrunSpec.getTaskSpec());
        this.setFuncSpec(runMlrunSpec.getFuncSpec());

        super.configure(data);
        this.setExtraSpecs(runMlrunSpec.getExtraSpecs());
    }
}
