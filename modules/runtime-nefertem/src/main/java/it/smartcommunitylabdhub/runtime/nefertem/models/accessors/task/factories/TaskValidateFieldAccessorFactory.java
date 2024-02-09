package it.smartcommunitylabdhub.runtime.nefertem.models.accessors.task.factories;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.accessors.AccessorFactory;
import it.smartcommunitylabdhub.runtime.nefertem.models.accessors.task.TaskValidateFieldAccessor;

import org.springframework.stereotype.Component;

@Component
public class TaskValidateFieldAccessorFactory implements AccessorFactory<TaskValidateFieldAccessor> {
    @Override
    public TaskValidateFieldAccessor create() {
        return new TaskValidateFieldAccessor();
    }
}
