package it.smartcommunitylabdhub.modules.nefertem.models.accessors.task;

import it.smartcommunitylabdhub.core.annotations.common.AccessorType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.models.accessors.AbstractFieldAccessor;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.TaskFieldAccessor;

@AccessorType(kind = "profile", entity = EntityName.TASK)
public class TaskProfileFieldAccessor
        extends AbstractFieldAccessor<TaskProfileFieldAccessor>
        implements TaskFieldAccessor<TaskProfileFieldAccessor> {

}
