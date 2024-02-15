package it.smartcommunitylabdhub.runtime.nefertem.specs.task;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import java.io.Serializable;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class TaskMetricSpecFactory implements SpecFactory<TaskMetricSpec> {

    @Override
    public TaskMetricSpec create() {
        return new TaskMetricSpec();
    }

    @Override
    public TaskMetricSpec create(Map<String, Serializable> data) {
        return new TaskMetricSpec(data);
    }
}
