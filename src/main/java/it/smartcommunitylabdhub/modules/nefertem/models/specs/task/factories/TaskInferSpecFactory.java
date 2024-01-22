package it.smartcommunitylabdhub.modules.nefertem.models.specs.task.factories;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecFactory;
import it.smartcommunitylabdhub.modules.nefertem.models.specs.task.TaskInferSpec;
import org.springframework.stereotype.Component;

@Component
public class TaskInferSpecFactory implements SpecFactory<TaskInferSpec> {
    @Override
    public TaskInferSpec create() {
        return new TaskInferSpec();
    }
}
