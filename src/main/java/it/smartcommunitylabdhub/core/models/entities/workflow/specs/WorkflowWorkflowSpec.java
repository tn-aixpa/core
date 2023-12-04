package it.smartcommunitylabdhub.core.models.entities.workflow.specs;


import it.smartcommunitylabdhub.core.annotations.common.SpecType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SpecType(kind = "workflow", entity = EntityName.WORKFLOW)
public class WorkflowWorkflowSpec extends WorkflowBaseSpec<WorkflowWorkflowSpec> {
    @Override
    protected void configureSpec(WorkflowWorkflowSpec workflowJobSpec) {
        super.configureSpec(workflowJobSpec);

//        throw new CoreException(
//                ErrorList.METHOD_NOT_IMPLEMENTED.getValue(),
//                ErrorList.METHOD_NOT_IMPLEMENTED.getReason(),
//                HttpStatus.INTERNAL_SERVER_ERROR
//        );
    }
}
