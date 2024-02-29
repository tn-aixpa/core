package it.smartcommunitylabdhub.runtime.kfp.specs.task;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import java.io.Serializable;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class TaskPipelineSpecFactory implements SpecFactory<TaskPipelineSpec> {

    @Override
    public TaskPipelineSpec create() {
        return new TaskPipelineSpec();
    }

    @Override
    public TaskPipelineSpec create(Map<String, Serializable> data) {
        return new TaskPipelineSpec(data);
    }
}
