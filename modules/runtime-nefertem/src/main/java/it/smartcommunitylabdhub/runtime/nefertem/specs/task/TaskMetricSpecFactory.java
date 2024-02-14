package it.smartcommunitylabdhub.runtime.nefertem.specs.task;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import org.springframework.stereotype.Component;

@Component
public class TaskMetricSpecFactory implements SpecFactory<TaskMetricSpec> {

    @Override
    public TaskMetricSpec create() {
        return new TaskMetricSpec();
    }
}
