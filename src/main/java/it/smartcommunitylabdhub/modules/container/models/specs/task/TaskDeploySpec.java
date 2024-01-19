package it.smartcommunitylabdhub.modules.container.models.specs.task;

import it.smartcommunitylabdhub.core.annotations.common.SpecType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.models.entities.task.specs.TaskBaseSpec;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SpecType(kind = "deploy", entity = EntityName.TASK, factory = TaskDeploySpec.class)
public class TaskDeploySpec extends TaskBaseSpec<TaskDeploySpec> {

    @Override
    protected void configureSpec(TaskDeploySpec taskDeploySpec) {
        super.configureSpec(taskDeploySpec);
    }
}
