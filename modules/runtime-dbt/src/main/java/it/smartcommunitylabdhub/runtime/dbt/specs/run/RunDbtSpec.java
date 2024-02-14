package it.smartcommunitylabdhub.runtime.dbt.specs.run;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.jackson.JacksonMapper;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.runtime.dbt.specs.function.FunctionDbtSpec;
import it.smartcommunitylabdhub.runtime.dbt.specs.task.TaskTransformSpec;
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
@SpecType(kind = "dbt+run", entity = EntityName.RUN)
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
