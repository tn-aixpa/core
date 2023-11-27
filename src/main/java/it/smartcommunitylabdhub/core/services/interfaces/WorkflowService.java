package it.smartcommunitylabdhub.core.services.interfaces;

import it.smartcommunitylabdhub.core.models.entities.run.Run;
import it.smartcommunitylabdhub.core.models.entities.workflow.WorkflowDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WorkflowService {
    List<WorkflowDTO> getWorkflows(Pageable pageable);

    WorkflowDTO createWorkflow(WorkflowDTO workflowDTO);

    WorkflowDTO getWorkflow(String uuid);

    WorkflowDTO updateWorkflow(WorkflowDTO workflowDTO, String uuid);

    boolean deleteWorkflow(String uuid);

    List<Run> getWorkflowRuns(String uuid);
}
