package it.smartcommunitylabdhub.runtime.nefertem.specs.task;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import org.springframework.stereotype.Component;

@Component
public class TaskValidateSpecFactory implements SpecFactory<TaskValidateSpec> {

    @Override
    public TaskValidateSpec create() {
        return new TaskValidateSpec();
    }
}
