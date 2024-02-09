package it.smartcommunitylabdhub.runtime.nefertem.models.accessors.task.factories;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.accessors.AccessorFactory;
import it.smartcommunitylabdhub.runtime.nefertem.models.accessors.task.TaskProfileFieldAccessor;

import org.springframework.stereotype.Component;

@Component
public class TaskProfileFieldAccessorFactory implements AccessorFactory<TaskProfileFieldAccessor> {
    @Override
    public TaskProfileFieldAccessor create() {
        return new TaskProfileFieldAccessor();
    }
}
