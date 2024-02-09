package it.smartcommunitylabdhub.core.models.entities.workflow.specs.factories;

import it.smartcommunitylabdhub.commons.infrastructure.factories.specs.SpecFactory;
import it.smartcommunitylabdhub.core.models.entities.workflow.specs.WorkflowWorkflowSpec;
import org.springframework.stereotype.Component;

@Component
public class WorkflowWorkflowSpecFactory
  implements SpecFactory<WorkflowWorkflowSpec> {

  @Override
  public WorkflowWorkflowSpec create() {
    return new WorkflowWorkflowSpec();
  }
}
