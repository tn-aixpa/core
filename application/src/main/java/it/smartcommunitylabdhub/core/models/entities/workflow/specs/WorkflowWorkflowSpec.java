package it.smartcommunitylabdhub.core.models.entities.workflow.specs;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.entities.workflow.specs.WorkflowBaseSpec;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SpecType(kind = "workflow", entity = EntityName.WORKFLOW)
public class WorkflowWorkflowSpec extends WorkflowBaseSpec {

    @Override
    public void configure(Map<String, Object> data) {
        super.configure(data);
    }
}
