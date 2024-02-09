package it.smartcommunitylabdhub.runtime.nefertem.models.specs.task.factories;

import it.smartcommunitylabdhub.commons.infrastructure.factories.specs.SpecFactory;
import it.smartcommunitylabdhub.runtime.nefertem.models.specs.task.TaskProfileSpec;
import org.springframework.stereotype.Component;

@Component
public class TaskProfileSpecFactory implements SpecFactory<TaskProfileSpec> {

  @Override
  public TaskProfileSpec create() {
    return new TaskProfileSpec();
  }
}
