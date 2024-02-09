package it.smartcommunitylabdhub.runtime.dbt.models.accessors.task;

import it.smartcommunitylabdhub.commons.annotations.common.AccessorType;
import it.smartcommunitylabdhub.commons.infrastructure.enums.EntityName;

@AccessorType(
  kind = "transform",
  entity = EntityName.TASK,
  factory = TaskValidateFieldAccessor.class
)
public class TaskTransformFieldAccessor
  extends AbstractFieldAccessor<TaskTransformFieldAccessor>
  implements TaskFieldAccessor<TaskTransformFieldAccessor> {}
