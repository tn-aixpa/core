package it.smartcommunitylabdhub.core.models.specs.workflow;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import java.io.Serializable;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class WorkflowWorkflowSpecFactory implements SpecFactory<WorkflowWorkflowSpec> {

    @Override
    public WorkflowWorkflowSpec create() {
        return new WorkflowWorkflowSpec();
    }

    @Override
    public WorkflowWorkflowSpec create(Map<String, Serializable> data) {
        WorkflowWorkflowSpec spec = new WorkflowWorkflowSpec();
        spec.configure(data);

        return spec;
    }
}
