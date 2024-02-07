package it.smartcommunitylabdhub.modules.mlrun.models.specs.run;


import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.core.annotations.common.SpecType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.models.entities.run.specs.RunBaseSpec;
import it.smartcommunitylabdhub.core.utils.jackson.JacksonMapper;
import it.smartcommunitylabdhub.modules.mlrun.models.specs.function.FunctionMlrunSpec;
import it.smartcommunitylabdhub.modules.mlrun.models.specs.task.TaskMlrunSpec;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SpecType(kind = "run+mlrun", entity = EntityName.RUN, factory = RunMlrunSpec.class)
public class RunMlrunSpec extends RunBaseSpec {

    @JsonProperty("task_spec")
    private TaskMlrunSpec taskSpec;

    @JsonProperty("func_spec")
    private FunctionMlrunSpec funcSpec;

    @Override
    public void configure(Map<String, Object> data) {

        RunMlrunSpec runMlrunSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
                data, RunMlrunSpec.class);

        this.setTaskSpec(runMlrunSpec.getTaskSpec());
        this.setFuncSpec(runMlrunSpec.getFuncSpec());

        super.configure(data);
        this.setExtraSpecs(runMlrunSpec.getExtraSpecs());
    }
}