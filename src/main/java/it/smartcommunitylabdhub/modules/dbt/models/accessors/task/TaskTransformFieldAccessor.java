package it.smartcommunitylabdhub.modules.dbt.models.accessors.task;

import it.smartcommunitylabdhub.core.annotations.common.AccessorType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.models.accessors.kinds.abstracts.AbstractFieldAccessor;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.TaskFieldAccessor;

@AccessorType(kind = "transform", entity = EntityName.TASK)
public class TaskTransformFieldAccessor
        extends AbstractFieldAccessor<TaskTransformFieldAccessor>
        implements TaskFieldAccessor<TaskTransformFieldAccessor> {

}
