package it.smartcommunitylabdhub.runtime.container.models.specs.task;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import org.springframework.stereotype.Component;

@Component
public class TaskJobSpecFactory implements SpecFactory<TaskJobSpec> {

    @Override
    public TaskJobSpec create() {
        return new TaskJobSpec();
    }
}
