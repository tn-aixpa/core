package it.smartcommunitylabdhub.core.services.interfaces;

import it.smartcommunitylabdhub.core.models.entities.run.Run;
import it.smartcommunitylabdhub.core.models.entities.workflow.Workflow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface WorkflowService {
    Page<Workflow> getWorkflows(Map<String, String> filter, Pageable pageable);

    Workflow createWorkflow(Workflow workflowDTO);

    Workflow getWorkflow(String uuid);

    Workflow updateWorkflow(Workflow workflowDTO, String uuid);

    boolean deleteWorkflow(String uuid);

    List<Run> getWorkflowRuns(String uuid);
}
