package it.smartcommunitylabdhub.core.models.accessors.kinds.tasks;

import it.smartcommunitylabdhub.core.annotations.common.AccessorType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.models.accessors.kinds.abstracts.AbstractFieldAccessor;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.TaskFieldAccessor;


@AccessorType(kind = "task", entity = EntityName.TASK)
public class TaskDefaultFieldAccessor
        extends AbstractFieldAccessor<TaskDefaultFieldAccessor>
        implements TaskFieldAccessor<TaskDefaultFieldAccessor> {
}
