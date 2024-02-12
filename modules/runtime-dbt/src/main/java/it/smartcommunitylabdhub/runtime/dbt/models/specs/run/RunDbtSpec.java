package it.smartcommunitylabdhub.runtime.dbt.models.specs.run;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.entities.run.specs.RunBaseSpec;
import it.smartcommunitylabdhub.commons.utils.jackson.JacksonMapper;
import it.smartcommunitylabdhub.runtime.dbt.models.specs.function.FunctionDbtSpec;
import it.smartcommunitylabdhub.runtime.dbt.models.specs.task.TaskTransformSpec;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SpecType(kind = "dbt+run", entity = EntityName.RUN, factory = RunDbtSpec.class)
public class RunDbtSpec extends RunBaseSpec {

    @JsonProperty("transform_spec")
    private TaskTransformSpec taskSpec;

    @JsonProperty("function_spec")
    private FunctionDbtSpec funcSpec;

    @Override
    public void configure(Map<String, Object> data) {
        RunDbtSpec runDbtSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(data, RunDbtSpec.class);

        this.setTaskSpec(runDbtSpec.getTaskSpec());
        this.setFuncSpec(runDbtSpec.getFuncSpec());

        super.configure(data);
        this.setExtraSpecs(runDbtSpec.getExtraSpecs());
    }
}
