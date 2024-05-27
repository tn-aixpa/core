package it.smartcommunitylabdhub.runtime.python.specs.task;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;

@Component
public class TaskJobSpecFactory implements SpecFactory<TaskJobSpec> {

    @Override
    public TaskJobSpec create() {
        return new TaskJobSpec();
    }

    @Override
    public TaskJobSpec create(Map<String, Serializable> data) {
        return new TaskJobSpec(data);
    }
}
