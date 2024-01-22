package it.smartcommunitylabdhub.modules.nefertem.models.specs.task.factories;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecFactory;
import it.smartcommunitylabdhub.modules.nefertem.models.specs.task.TaskProfileSpec;
import org.springframework.stereotype.Component;

@Component
public class TaskProfileSpecFactory implements SpecFactory<TaskProfileSpec> {
    @Override
    public TaskProfileSpec create() {
        return new TaskProfileSpec();
    }
}
