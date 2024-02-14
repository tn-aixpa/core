package it.smartcommunitylabdhub.runtime.nefertem.specs.task;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import org.springframework.stereotype.Component;

@Component
public class TaskInferSpecFactory implements SpecFactory<TaskInferSpec> {

    @Override
    public TaskInferSpec create() {
        return new TaskInferSpec();
    }
}
