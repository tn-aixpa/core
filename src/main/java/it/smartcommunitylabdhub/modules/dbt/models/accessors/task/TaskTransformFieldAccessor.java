package it.smartcommunitylabdhub.modules.dbt.models.accessors.task;

import it.smartcommunitylabdhub.core.annotations.common.AccessorType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.models.accessors.AbstractFieldAccessor;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.TaskFieldAccessor;
import it.smartcommunitylabdhub.modules.nefertem.models.accessors.task.TaskValidateFieldAccessor;

@AccessorType(kind = "transform", entity = EntityName.TASK, factory = TaskValidateFieldAccessor.class)
public class TaskTransformFieldAccessor
        extends AbstractFieldAccessor<TaskTransformFieldAccessor>
        implements TaskFieldAccessor<TaskTransformFieldAccessor> {

}
