package it.smartcommunitylabdhub.core.models.queries.services;

import it.smartcommunitylabdhub.commons.exceptions.SystemException;
import it.smartcommunitylabdhub.commons.models.queries.SearchFilter;
import it.smartcommunitylabdhub.commons.models.run.Run;
import it.smartcommunitylabdhub.commons.services.entities.RunService;
import it.smartcommunitylabdhub.core.models.entities.RunEntity;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/*
 * Searchable service for managing run
 */
public interface SearchableRunService extends RunService {
    /**
     * List all runs, with optional filters
     * @param pageable
     * @param filter
     * @return
     */
    Page<Run> searchRuns(Pageable pageable, @Nullable SearchFilter<RunEntity> filter) throws SystemException;

    /**
     * List the runs for a given project, with optional filters
     * @param project
     * @param pageable
     * @param filter
     * @return
     */
    Page<Run> searchRunsByProject(@NotNull String project, Pageable pageable, @Nullable SearchFilter<RunEntity> filter)
        throws SystemException;
}
