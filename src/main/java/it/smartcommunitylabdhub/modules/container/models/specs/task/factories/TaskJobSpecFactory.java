package it.smartcommunitylabdhub.modules.container.models.specs.task.factories;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecFactory;
import it.smartcommunitylabdhub.modules.container.models.specs.task.TaskJobSpec;
import org.springframework.stereotype.Component;

@Component
public class TaskJobSpecFactory implements SpecFactory<TaskJobSpec> {
    @Override
    public TaskJobSpec create() {
        return new TaskJobSpec();
    }
}
