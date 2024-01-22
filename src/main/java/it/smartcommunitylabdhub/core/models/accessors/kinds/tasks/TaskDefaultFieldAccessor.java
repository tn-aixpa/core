package it.smartcommunitylabdhub.core.models.accessors.kinds.tasks;

import it.smartcommunitylabdhub.core.annotations.common.AccessorType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.models.accessors.AbstractFieldAccessor;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.TaskFieldAccessor;


@AccessorType(kind = "task", entity = EntityName.TASK, factory = TaskDefaultFieldAccessor.class)
public class TaskDefaultFieldAccessor
        extends AbstractFieldAccessor<TaskDefaultFieldAccessor>
        implements TaskFieldAccessor<TaskDefaultFieldAccessor> {
}
