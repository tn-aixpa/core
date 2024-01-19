package it.smartcommunitylabdhub.modules.container.models.specs.task;

import it.smartcommunitylabdhub.core.annotations.common.SpecType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.models.entities.task.specs.TaskBaseSpec;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SpecType(kind = "job", entity = EntityName.TASK, factory = TaskJobSpec.class)
public class TaskJobSpec extends TaskBaseSpec<TaskJobSpec> {

    @Override
    protected void configureSpec(TaskJobSpec taskDeploySpec) {
        super.configureSpec(taskDeploySpec);
    }
}
