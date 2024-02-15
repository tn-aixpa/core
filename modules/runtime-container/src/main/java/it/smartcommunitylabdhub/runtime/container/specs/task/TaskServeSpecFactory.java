package it.smartcommunitylabdhub.runtime.container.specs.task;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import java.io.Serializable;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class TaskServeSpecFactory implements SpecFactory<TaskServeSpec> {

    @Override
    public TaskServeSpec create() {
        return new TaskServeSpec();
    }

    @Override
    public TaskServeSpec create(Map<String, Serializable> data) {
        return new TaskServeSpec(data);
    }
}
