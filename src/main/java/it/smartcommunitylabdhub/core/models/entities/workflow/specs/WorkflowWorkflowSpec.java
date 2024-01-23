package it.smartcommunitylabdhub.core.models.entities.workflow.specs;


import it.smartcommunitylabdhub.core.annotations.common.SpecType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@SpecType(kind = "workflow", entity = EntityName.WORKFLOW, factory = WorkflowWorkflowSpec.class)
public class WorkflowWorkflowSpec extends WorkflowBaseSpec {
    @Override
    public void configure(Map<String, Object> data) {
        super.configure(data);
    }
}
