package it.smartcommunitylabdhub.core.models.indexers;

import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.exceptions.SystemException;
import jakarta.validation.constraints.NotNull;
import org.springframework.scheduling.annotation.Async;

public interface IndexableWorkflowService {
    public void indexWorkflow(@NotNull String id) throws NoSuchEntityException, SystemException;

    @Async
    public void reindexWorkflows() throws SystemException;
}
