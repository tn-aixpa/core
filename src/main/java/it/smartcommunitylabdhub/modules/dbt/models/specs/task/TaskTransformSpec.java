package it.smartcommunitylabdhub.modules.dbt.models.specs.task;

import it.smartcommunitylabdhub.core.annotations.common.SpecType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.models.entities.task.specs.TaskBaseSpec;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SpecType(kind = "transform", entity = EntityName.TASK, factory = TaskTransformSpec.class)
public class TaskTransformSpec extends TaskBaseSpec<TaskTransformSpec> {

    @Override
    protected void configureSpec(TaskTransformSpec taskTransformSpec) {
        super.configureSpec(taskTransformSpec);
    }
}
