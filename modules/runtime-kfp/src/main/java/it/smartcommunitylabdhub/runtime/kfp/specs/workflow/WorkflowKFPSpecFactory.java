package it.smartcommunitylabdhub.runtime.kfp.specs.workflow;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import java.io.Serializable;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class WorkflowKFPSpecFactory implements SpecFactory<WorkflowKFPSpec> {

    @Override
    public WorkflowKFPSpec create() {
        return new WorkflowKFPSpec();
    }

    @Override
    public WorkflowKFPSpec create(Map<String, Serializable> data) {
        return new WorkflowKFPSpec(data);
    }
}
