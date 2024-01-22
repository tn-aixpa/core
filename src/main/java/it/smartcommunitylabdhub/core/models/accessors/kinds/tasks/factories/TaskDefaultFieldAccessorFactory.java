package it.smartcommunitylabdhub.core.models.accessors.kinds.tasks.factories;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.accessors.AccessorFactory;
import it.smartcommunitylabdhub.core.models.accessors.kinds.tasks.TaskDefaultFieldAccessor;
import org.springframework.stereotype.Component;

@Component
public class TaskDefaultFieldAccessorFactory implements AccessorFactory<TaskDefaultFieldAccessor> {
    @Override
    public TaskDefaultFieldAccessor create() {
        return new TaskDefaultFieldAccessor();
    }
}
