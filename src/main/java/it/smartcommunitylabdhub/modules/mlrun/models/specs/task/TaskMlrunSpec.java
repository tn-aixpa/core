package it.smartcommunitylabdhub.modules.mlrun.models.specs.task;

import it.smartcommunitylabdhub.core.annotations.common.SpecType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.models.entities.task.specs.TaskBaseSpec;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SpecType(kind = "mlrun", entity = EntityName.TASK)
public class TaskMlrunSpec extends TaskBaseSpec<TaskMlrunSpec> {

    @Override
    protected void configureSpec(TaskMlrunSpec taskMlrunSpec) {
        super.configureSpec(taskMlrunSpec);
    }
}
