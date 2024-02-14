package it.smartcommunitylabdhub.runtime.nefertem.specs.task;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import org.springframework.stereotype.Component;

@Component
public class TaskProfileSpecFactory implements SpecFactory<TaskProfileSpec> {

    @Override
    public TaskProfileSpec create() {
        return new TaskProfileSpec();
    }
}
