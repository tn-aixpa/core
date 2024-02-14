package it.smartcommunitylabdhub.runtime.nefertem.models.specs.task.factories;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import it.smartcommunitylabdhub.runtime.nefertem.models.specs.task.TaskInferSpec;
import org.springframework.stereotype.Component;

@Component
public class TaskInferSpecFactory implements SpecFactory<TaskInferSpec> {

    @Override
    public TaskInferSpec create() {
        return new TaskInferSpec();
    }
}
