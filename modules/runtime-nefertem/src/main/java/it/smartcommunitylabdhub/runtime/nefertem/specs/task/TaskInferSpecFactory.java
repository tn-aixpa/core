package it.smartcommunitylabdhub.runtime.nefertem.specs.task;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import java.io.Serializable;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class TaskInferSpecFactory implements SpecFactory<TaskInferSpec> {

    @Override
    public TaskInferSpec create() {
        return new TaskInferSpec();
    }

    @Override
    public TaskInferSpec create(Map<String, Serializable> data) {
        return new TaskInferSpec(data);
    }
}
