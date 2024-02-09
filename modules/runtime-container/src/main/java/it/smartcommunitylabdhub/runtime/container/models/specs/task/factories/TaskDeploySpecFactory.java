package it.smartcommunitylabdhub.runtime.container.models.specs.task.factories;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecFactory;
import it.smartcommunitylabdhub.runtime.container.models.specs.task.TaskDeploySpec;

import org.springframework.stereotype.Component;

@Component
public class TaskDeploySpecFactory implements SpecFactory<TaskDeploySpec> {
    @Override
    public TaskDeploySpec create() {
        return new TaskDeploySpec();
    }
}
