package it.smartcommunitylabdhub.runtime.container.specs.task;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import org.springframework.stereotype.Component;

@Component
public class TaskDeploySpecFactory implements SpecFactory<TaskDeploySpec> {

    @Override
    public TaskDeploySpec create() {
        return new TaskDeploySpec();
    }
}
