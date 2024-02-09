package it.smartcommunitylabdhub.modules.mlrun.models.specs.task.factories;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecFactory;
import it.smartcommunitylabdhub.modules.mlrun.models.specs.task.TaskMlrunSpec;
import org.springframework.stereotype.Component;

@Component
public class TaskMlrunSpecFactory implements SpecFactory<TaskMlrunSpec> {
    @Override
    public TaskMlrunSpec create() {
        return new TaskMlrunSpec();
    }
}
