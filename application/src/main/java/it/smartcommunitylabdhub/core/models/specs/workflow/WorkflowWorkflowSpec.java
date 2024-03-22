package it.smartcommunitylabdhub.core.models.specs.workflow;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.entities.workflow.WorkflowBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SpecType(kind = "workflow", entity = EntityName.WORKFLOW)
public class WorkflowWorkflowSpec extends WorkflowBaseSpec {

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);
    }
}
