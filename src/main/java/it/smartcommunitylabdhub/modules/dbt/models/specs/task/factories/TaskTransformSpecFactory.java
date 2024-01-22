package it.smartcommunitylabdhub.modules.dbt.models.specs.task.factories;


import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecFactory;
import it.smartcommunitylabdhub.modules.dbt.models.specs.task.TaskTransformSpec;
import org.springframework.stereotype.Component;

@Component
public class TaskTransformSpecFactory implements SpecFactory<TaskTransformSpec> {
    @Override
    public TaskTransformSpec create() {
        return new TaskTransformSpec();
    }
}
