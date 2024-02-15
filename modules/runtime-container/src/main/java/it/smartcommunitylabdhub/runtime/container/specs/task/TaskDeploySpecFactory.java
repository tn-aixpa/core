package it.smartcommunitylabdhub.runtime.container.specs.task;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import java.io.Serializable;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class TaskDeploySpecFactory implements SpecFactory<TaskDeploySpec> {

    @Override
    public TaskDeploySpec create() {
        return new TaskDeploySpec();
    }

    @Override
    public TaskDeploySpec create(Map<String, Serializable> data) {
        return new TaskDeploySpec(data);
    }
}
