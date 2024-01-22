package it.smartcommunitylabdhub.modules.nefertem.models.accessors.task.factories;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.accessors.AccessorFactory;
import it.smartcommunitylabdhub.modules.nefertem.models.accessors.task.TaskMetricFieldAccessor;
import org.springframework.stereotype.Component;

@Component
public class TaskMetricFieldAccessorFactory implements AccessorFactory<TaskMetricFieldAccessor> {
    @Override
    public TaskMetricFieldAccessor create() {
        return new TaskMetricFieldAccessor();
    }
}
