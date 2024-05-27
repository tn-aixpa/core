package it.smartcommunitylabdhub.runtime.python.specs.task;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;

@Component
public class TaskBuildSpecFactory implements SpecFactory<TaskBuildSpec> {

    @Override
    public TaskBuildSpec create() {
        return new TaskBuildSpec();
    }

    @Override
    public TaskBuildSpec create(Map<String, Serializable> data) {
        return new TaskBuildSpec(data);
    }
}
