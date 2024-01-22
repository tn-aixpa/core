package it.smartcommunitylabdhub.modules.nefertem.models.specs.task.factories;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecFactory;
import it.smartcommunitylabdhub.modules.nefertem.models.specs.task.TaskMetricSpec;
import org.springframework.stereotype.Component;

@Component
public class TaskMetricSpecFactory implements SpecFactory<TaskMetricSpec> {
    @Override
    public TaskMetricSpec create() {
        return new TaskMetricSpec();
    }
}
