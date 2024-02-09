package it.smartcommunitylabdhub.runtime.nefertem.models.specs.task.factories;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecFactory;
import it.smartcommunitylabdhub.runtime.nefertem.models.specs.task.TaskValidateSpec;

import org.springframework.stereotype.Component;

@Component
public class TaskValidateSpecFactory implements SpecFactory<TaskValidateSpec> {
    @Override
    public TaskValidateSpec create() {
        return new TaskValidateSpec();
    }
}
