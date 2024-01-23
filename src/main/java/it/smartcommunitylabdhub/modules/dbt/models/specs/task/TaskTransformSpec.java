package it.smartcommunitylabdhub.modules.dbt.models.specs.task;

import it.smartcommunitylabdhub.core.annotations.common.SpecType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.models.entities.task.specs.TaskBaseSpec;
import it.smartcommunitylabdhub.core.utils.jackson.JacksonMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@SpecType(kind = "transform", entity = EntityName.TASK, factory = TaskTransformSpec.class)
public class TaskTransformSpec extends TaskBaseSpec {

    @Override
    public void configure(Map<String, Object> data) {

        TaskTransformSpec taskTransformSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
                data, TaskTransformSpec.class);

        super.configure(data);
        this.setExtraSpecs(taskTransformSpec.getExtraSpecs());
    }
}
