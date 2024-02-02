package it.smartcommunitylabdhub.modules.container.models.specs.task.factories;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecFactory;
import it.smartcommunitylabdhub.modules.container.models.specs.task.TaskServeSpec;
import org.springframework.stereotype.Component;

@Component
public class TaskServeSpecFactory implements SpecFactory<TaskServeSpec> {
    @Override
    public TaskServeSpec create() {
        return new TaskServeSpec();
    }
}
