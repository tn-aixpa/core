package it.smartcommunitylabdhub.runtime.nefertem.specs.task;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import java.io.Serializable;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class TaskProfileSpecFactory implements SpecFactory<TaskProfileSpec> {

    @Override
    public TaskProfileSpec create() {
        return new TaskProfileSpec();
    }

    @Override
    public TaskProfileSpec create(Map<String, Serializable> data) {
        return new TaskProfileSpec(data);
    }
}
