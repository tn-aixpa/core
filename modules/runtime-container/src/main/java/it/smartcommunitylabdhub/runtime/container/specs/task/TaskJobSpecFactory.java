package it.smartcommunitylabdhub.runtime.container.specs.task;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import java.io.Serializable;
import java.util.Map;
import org.springframework.stereotype.Component;

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
