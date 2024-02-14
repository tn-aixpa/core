package it.smartcommunitylabdhub.runtime.mlrun.specs.task;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import org.springframework.stereotype.Component;

@Component
public class TaskMlrunSpecFactory implements SpecFactory<TaskMlrunSpec> {

    @Override
    public TaskMlrunSpec create() {
        return new TaskMlrunSpec();
    }
}
