package it.smartcommunitylabdhub.core.models.indexers;

import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import jakarta.validation.constraints.NotNull;
import org.springframework.scheduling.annotation.Async;

public interface IndexableModelService {
    public void indexModel(@NotNull String id) throws NoSuchEntityException;

    @Async
    public void reindexModels();
}
