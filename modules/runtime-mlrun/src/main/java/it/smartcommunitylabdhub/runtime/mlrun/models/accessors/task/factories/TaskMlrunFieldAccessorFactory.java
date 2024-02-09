package it.smartcommunitylabdhub.modules.mlrun.models.accessors.task.factories;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.accessors.AccessorFactory;
import it.smartcommunitylabdhub.modules.mlrun.models.accessors.task.TaskMlrunFieldAccessor;
import org.springframework.stereotype.Component;

@Component
public class TaskMlrunFieldAccessorFactory implements AccessorFactory<TaskMlrunFieldAccessor> {
    @Override
    public TaskMlrunFieldAccessor create() {
        return new TaskMlrunFieldAccessor();
    }
}
