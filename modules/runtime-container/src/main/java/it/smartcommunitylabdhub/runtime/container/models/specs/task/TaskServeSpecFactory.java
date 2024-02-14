package it.smartcommunitylabdhub.runtime.container.models.specs.task;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import org.springframework.stereotype.Component;

@Component
public class TaskServeSpecFactory implements SpecFactory<TaskServeSpec> {

    @Override
    public TaskServeSpec create() {
        return new TaskServeSpec();
    }
}
