package it.smartcommunitylabdhub.modules.nefertem.models.accessors.task;

import it.smartcommunitylabdhub.core.annotations.common.AccessorType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.models.accessors.kinds.abstracts.AbstractFieldAccessor;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.TaskFieldAccessor;

@AccessorType(kind = "metric", entity = EntityName.TASK)
public class TaskMetricFieldAccessor
        extends AbstractFieldAccessor<TaskMetricFieldAccessor>
        implements TaskFieldAccessor<TaskMetricFieldAccessor> {

}
