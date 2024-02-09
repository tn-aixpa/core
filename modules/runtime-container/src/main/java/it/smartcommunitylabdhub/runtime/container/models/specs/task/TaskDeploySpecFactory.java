package it.smartcommunitylabdhub.runtime.container.models.specs.task;

import it.smartcommunitylabdhub.commons.infrastructure.factories.specs.SpecFactory;
import org.springframework.stereotype.Component;

@Component
public class TaskDeploySpecFactory implements SpecFactory<TaskDeploySpec> {

    @Override
    public TaskDeploySpec create() {
        return new TaskDeploySpec();
    }
}
