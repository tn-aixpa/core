package it.smartcommunitylabdhub.modules.dbt.models.specs.run;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.core.annotations.common.SpecType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.models.entities.run.specs.RunBaseSpec;
import it.smartcommunitylabdhub.core.utils.jackson.JacksonMapper;
import it.smartcommunitylabdhub.modules.dbt.models.specs.function.FunctionDbtSpec;
import it.smartcommunitylabdhub.modules.dbt.models.specs.task.TaskTransformSpec;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SpecType(kind = "run+dbt", entity = EntityName.RUN, factory = RunDbtSpec.class)
public class RunDbtSpec extends RunBaseSpec {

    @JsonProperty("task_spec")
    private TaskTransformSpec taskSpec;

    @JsonProperty("func_spec")
    private FunctionDbtSpec funcSpec;

    @Override
    public void configure(Map<String, Object> data) {

        RunDbtSpec runDbtSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
                data, RunDbtSpec.class);

        this.setTaskSpec(runDbtSpec.getTaskSpec());
        this.setFuncSpec(runDbtSpec.getFuncSpec());

        super.configure(data);
        this.setExtraSpecs(runDbtSpec.getExtraSpecs());
    }
}