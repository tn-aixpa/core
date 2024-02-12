package it.smartcommunitylabdhub.commons.services.interfaces;

import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.entities.workflow.Workflow;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WorkflowService {
    Page<Workflow> getWorkflows(Map<String, String> filter, Pageable pageable);

    Workflow createWorkflow(Workflow workflowDTO);

    Workflow getWorkflow(String uuid);

    Workflow updateWorkflow(Workflow workflowDTO, String uuid);

    boolean deleteWorkflow(String uuid);

    List<Run> getWorkflowRuns(String uuid);
}
