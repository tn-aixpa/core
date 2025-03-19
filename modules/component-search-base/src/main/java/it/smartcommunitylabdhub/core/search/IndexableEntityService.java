package it.smartcommunitylabdhub.core.search;

import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.exceptions.SystemException;
import it.smartcommunitylabdhub.core.models.base.BaseEntity;
import jakarta.validation.constraints.NotNull;
import org.springframework.scheduling.annotation.Async;

public interface IndexableEntityService<T extends BaseEntity> {
    public void indexOne(@NotNull String id) throws NoSuchEntityException, SystemException;

    @Async
    public void reindexAll() throws SystemException;
}
