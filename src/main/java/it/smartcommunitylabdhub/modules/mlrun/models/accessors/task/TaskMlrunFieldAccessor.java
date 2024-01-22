package it.smartcommunitylabdhub.modules.mlrun.models.accessors.task;

import it.smartcommunitylabdhub.core.annotations.common.AccessorType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.models.accessors.AbstractFieldAccessor;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.TaskFieldAccessor;

@AccessorType(kind = "mlrun", entity = EntityName.TASK, factory = TaskMlrunFieldAccessor.class)
public class TaskMlrunFieldAccessor
        extends AbstractFieldAccessor<TaskMlrunFieldAccessor>
        implements TaskFieldAccessor<TaskMlrunFieldAccessor> {
}
