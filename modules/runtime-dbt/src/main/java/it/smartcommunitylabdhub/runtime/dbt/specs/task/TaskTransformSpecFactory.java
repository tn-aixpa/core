package it.smartcommunitylabdhub.runtime.dbt.specs.task;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import org.springframework.stereotype.Component;

@Component
public class TaskTransformSpecFactory implements SpecFactory<TaskTransformSpec> {

    @Override
    public TaskTransformSpec create() {
        return new TaskTransformSpec();
    }
}
