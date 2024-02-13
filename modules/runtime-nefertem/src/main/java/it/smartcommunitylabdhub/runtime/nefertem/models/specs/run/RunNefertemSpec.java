package it.smartcommunitylabdhub.runtime.nefertem.models.specs.run;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.entities.run.specs.RunBaseSpec;
import it.smartcommunitylabdhub.commons.utils.jackson.JacksonMapper;
import it.smartcommunitylabdhub.runtime.nefertem.models.specs.function.FunctionNefertemSpec;
import it.smartcommunitylabdhub.runtime.nefertem.models.specs.task.TaskInferSpec;
import it.smartcommunitylabdhub.runtime.nefertem.models.specs.task.TaskMetricSpec;
import it.smartcommunitylabdhub.runtime.nefertem.models.specs.task.TaskProfileSpec;
import it.smartcommunitylabdhub.runtime.nefertem.models.specs.task.TaskValidateSpec;
import java.util.Map;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SpecType(kind = "nefertem+run", entity = EntityName.RUN, factory = RunNefertemSpec.class)
public class RunNefertemSpec extends RunBaseSpec {

    @JsonProperty("infer_spec")
    private TaskInferSpec taskInferSpec;

    @JsonProperty("metric_spec")
    private TaskMetricSpec taskMetricSpec;

    @JsonProperty("profile_spec")
    private TaskProfileSpec taskProfileSpec;

    @JsonProperty("validate_spec")
    private TaskValidateSpec taskValidateSpec;

    @JsonProperty("function_spec")
    private FunctionNefertemSpec funcSpec;

    @Override
    public void configure(Map<String, Object> data) {
        RunNefertemSpec runNefertemSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(data, RunNefertemSpec.class);

        this.setTaskInferSpec(runNefertemSpec.getTaskInferSpec());
        this.setTaskMetricSpec(runNefertemSpec.getTaskMetricSpec());
        this.setTaskProfileSpec(runNefertemSpec.getTaskProfileSpec());
        this.setTaskValidateSpec(runNefertemSpec.getTaskValidateSpec());

        this.setFuncSpec(runNefertemSpec.getFuncSpec());

        super.configure(data);
        this.setExtraSpecs(runNefertemSpec.getExtraSpecs());
    }
}
