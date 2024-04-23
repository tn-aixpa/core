package it.smartcommunitylabdhub.runtime.mlrun.specs.task;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import java.io.Serializable;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class TaskMlrunBuildSpecFactory implements SpecFactory<TaskMlrunBuildSpec> {

    @Override
    public TaskMlrunBuildSpec create() {
        return new TaskMlrunBuildSpec();
    }

    @Override
    public TaskMlrunBuildSpec create(Map<String, Serializable> data) {
        return new TaskMlrunBuildSpec(data);
    }
}
