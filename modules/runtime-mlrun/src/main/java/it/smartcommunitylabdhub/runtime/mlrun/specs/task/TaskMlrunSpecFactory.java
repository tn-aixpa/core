package it.smartcommunitylabdhub.runtime.mlrun.specs.task;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import java.io.Serializable;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class TaskMlrunSpecFactory implements SpecFactory<TaskMlrunSpec> {

    @Override
    public TaskMlrunSpec create() {
        return new TaskMlrunSpec();
    }

    @Override
    public TaskMlrunSpec create(Map<String, Serializable> data) {
        return new TaskMlrunSpec(data);
    }
}
