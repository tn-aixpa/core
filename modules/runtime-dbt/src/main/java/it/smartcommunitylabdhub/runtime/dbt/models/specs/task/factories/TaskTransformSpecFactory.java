package it.smartcommunitylabdhub.runtime.dbt.models.specs.task.factories;

import it.smartcommunitylabdhub.commons.infrastructure.factories.specs.SpecFactory;
import it.smartcommunitylabdhub.runtime.dbt.models.specs.task.TaskTransformSpec;

import org.springframework.stereotype.Component;

@Component
public class TaskTransformSpecFactory
  implements SpecFactory<TaskTransformSpec> {

  @Override
  public TaskTransformSpec create() {
    return new TaskTransformSpec();
  }
}
