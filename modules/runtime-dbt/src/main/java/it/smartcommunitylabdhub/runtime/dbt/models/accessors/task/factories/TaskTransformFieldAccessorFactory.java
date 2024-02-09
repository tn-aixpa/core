package it.smartcommunitylabdhub.runtime.dbt.models.accessors.task.factories;

import it.smartcommunitylabdhub.commons.infrastructure.factories.accessors.AccessorFactory;
import it.smartcommunitylabdhub.runtime.dbt.models.accessors.task.TaskTransformFieldAccessor;

import org.springframework.stereotype.Component;

@Component
public class TaskTransformFieldAccessorFactory
  implements AccessorFactory<TaskTransformFieldAccessor> {

  @Override
  public TaskTransformFieldAccessor create() {
    return new TaskTransformFieldAccessor();
  }
}
