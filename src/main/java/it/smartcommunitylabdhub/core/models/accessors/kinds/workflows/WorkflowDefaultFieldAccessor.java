package it.smartcommunitylabdhub.core.models.accessors.kinds.workflows;

import it.smartcommunitylabdhub.core.annotations.common.AccessorType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.models.accessors.AbstractFieldAccessor;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.WorkflowFieldAccessor;


@AccessorType(kind = "workflow", entity = EntityName.WORKFLOW, factory = WorkflowDefaultFieldAccessor.class)
public class WorkflowDefaultFieldAccessor
        extends AbstractFieldAccessor<WorkflowDefaultFieldAccessor>
        implements WorkflowFieldAccessor<WorkflowDefaultFieldAccessor> {
}
