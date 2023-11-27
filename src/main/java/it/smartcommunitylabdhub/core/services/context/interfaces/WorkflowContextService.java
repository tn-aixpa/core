package it.smartcommunitylabdhub.core.services.context.interfaces;

import it.smartcommunitylabdhub.core.models.entities.workflow.Workflow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WorkflowContextService {

    Workflow createWorkflow(String projectName, Workflow workflowDTO);

    Page<Workflow> getByProjectNameAndWorkflowName(
            String projectName, String workflowName, Pageable pageable);

    Page<Workflow> getLatestByProjectName(
            String projectName, Pageable pageable);

    Workflow getByProjectAndWorkflowAndUuid(
            String projectName, String workflowName, String uuid);

    Workflow getLatestByProjectNameAndWorkflowName(
            String projectName, String workflowName);

    Workflow createOrUpdateWorkflow(String projectName, String workflowName,
                                    Workflow workflowDTO);

    Workflow updateWorkflow(String projectName, String workflowName, String uuid,
                            Workflow workflowDTO);

    Boolean deleteSpecificWorkflowVersion(String projectName, String workflowName, String uuid);

    Boolean deleteAllWorkflowVersions(String projectName, String workflowName);
}
