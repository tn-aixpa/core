package it.smartcommunitylabdhub.modules.nefertem.models.specs.run;


import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.core.annotations.common.SpecType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.models.entities.run.specs.RunBaseSpec;
import it.smartcommunitylabdhub.core.utils.jackson.JacksonMapper;
import it.smartcommunitylabdhub.modules.nefertem.models.specs.function.FunctionNefertemSpec;
import it.smartcommunitylabdhub.modules.nefertem.models.specs.task.TaskInferSpec;
import it.smartcommunitylabdhub.modules.nefertem.models.specs.task.TaskMetricSpec;
import it.smartcommunitylabdhub.modules.nefertem.models.specs.task.TaskProfileSpec;
import it.smartcommunitylabdhub.modules.nefertem.models.specs.task.TaskValidateSpec;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SpecType(kind = "run", runtime = "nefertem", entity = EntityName.RUN, factory = RunNefertemSpec.class)
public class RunNefertemSpec extends RunBaseSpec {

    @JsonProperty("task_infer_spec")
    private TaskInferSpec taskInferSpec;

    @JsonProperty("task_metric_spec")
    private TaskMetricSpec taskMetricSpec;

    @JsonProperty("task_profile_spec")
    private TaskProfileSpec taskProfileSpec;

    @JsonProperty("task_validate_spec")
    private TaskValidateSpec taskValidateSpec;


    @JsonProperty("func_nefertem_spec")
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