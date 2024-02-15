package it.smartcommunitylabdhub.runtime.dbt.specs.task;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import java.io.Serializable;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class TaskTransformSpecFactory implements SpecFactory<TaskTransformSpec> {

    @Override
    public TaskTransformSpec create() {
        return new TaskTransformSpec();
    }

    @Override
    public TaskTransformSpec create(Map<String, Serializable> data) {
        return new TaskTransformSpec(data);
    }
}
