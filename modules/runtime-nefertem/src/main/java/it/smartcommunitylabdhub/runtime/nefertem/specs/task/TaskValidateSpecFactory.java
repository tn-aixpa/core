package it.smartcommunitylabdhub.runtime.nefertem.specs.task;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import java.io.Serializable;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class TaskValidateSpecFactory implements SpecFactory<TaskValidateSpec> {

    @Override
    public TaskValidateSpec create() {
        return new TaskValidateSpec();
    }

    @Override
    public TaskValidateSpec create(Map<String, Serializable> data) {
        return new TaskValidateSpec(data);
    }
}
