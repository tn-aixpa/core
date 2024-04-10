package it.smartcommunitylabdhub.core.models.indexers;

import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import jakarta.validation.constraints.NotNull;
import org.springframework.scheduling.annotation.Async;

public interface IndexableFunctionService {
    public void indexFunction(@NotNull String id) throws NoSuchEntityException;

    @Async
    public void reindexFunctions();
}
