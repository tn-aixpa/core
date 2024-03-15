package it.smartcommunitylabdhub.runtime.mlrun.specs.task;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import java.io.Serializable;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class TaskMlrunSpecFactory implements SpecFactory<TaskMlrunJobSpec> {

    @Override
    public TaskMlrunJobSpec create() {
        return new TaskMlrunJobSpec();
    }

    @Override
    public TaskMlrunJobSpec create(Map<String, Serializable> data) {
        return new TaskMlrunJobSpec(data);
    }
}
