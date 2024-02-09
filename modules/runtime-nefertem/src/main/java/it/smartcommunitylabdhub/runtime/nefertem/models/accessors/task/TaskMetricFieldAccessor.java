package it.smartcommunitylabdhub.runtime.nefertem.models.accessors.task;

import it.smartcommunitylabdhub.commons.annotations.common.AccessorType;
import it.smartcommunitylabdhub.commons.models.accessors.kinds.interfaces.TaskFieldAccessor;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.models.accessors.AbstractFieldAccessor;

@AccessorType(kind = "metric", entity = EntityName.TASK, factory = TaskMetricFieldAccessor.class)
public class TaskMetricFieldAccessor
        extends AbstractFieldAccessor<TaskMetricFieldAccessor>
        implements TaskFieldAccessor<TaskMetricFieldAccessor> {

}
