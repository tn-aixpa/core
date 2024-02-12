package it.smartcommunitylabdhub.runtime.nefertem.models.specs.task.factories;

import it.smartcommunitylabdhub.commons.infrastructure.factories.specs.SpecFactory;
import it.smartcommunitylabdhub.runtime.nefertem.models.specs.task.TaskMetricSpec;
import org.springframework.stereotype.Component;

@Component
public class TaskMetricSpecFactory implements SpecFactory<TaskMetricSpec> {

    @Override
    public TaskMetricSpec create() {
        return new TaskMetricSpec();
    }
}
