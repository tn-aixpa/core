package it.smartcommunitylabdhub.runtime.nefertem.models.accessors.task;

import it.smartcommunitylabdhub.commons.annotations.common.AccessorType;
import it.smartcommunitylabdhub.commons.models.accessors.kinds.interfaces.TaskFieldAccessor;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.models.accessors.AbstractFieldAccessor;

@AccessorType(kind = "validate", entity = EntityName.TASK, factory = TaskValidateFieldAccessor.class)
public class TaskValidateFieldAccessor
        extends AbstractFieldAccessor<TaskValidateFieldAccessor>
        implements TaskFieldAccessor<TaskValidateFieldAccessor> {

}
