package it.smartcommunitylabdhub.runtime.mlrun.models.specs.task;

import it.smartcommunitylabdhub.commons.infrastructure.factories.specs.SpecFactory;
import org.springframework.stereotype.Component;

@Component
public class TaskMlrunSpecFactory implements SpecFactory<TaskMlrunSpec> {

  @Override
  public TaskMlrunSpec create() {
    return new TaskMlrunSpec();
  }
}
