package it.smartcommunitylabdhub.core.models.accessors.kinds.workflows.factories;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.accessors.AccessorFactory;
import it.smartcommunitylabdhub.core.models.accessors.kinds.workflows.WorkflowDefaultFieldAccessor;
import org.springframework.stereotype.Component;

@Component
public class WorkflowDefaultFieldAccessorFactory implements AccessorFactory<WorkflowDefaultFieldAccessor> {
    @Override
    public WorkflowDefaultFieldAccessor create() {
        return new WorkflowDefaultFieldAccessor();
    }
}
