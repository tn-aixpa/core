package it.smartcommunitylabdhub.core.models.queries.services;

import it.smartcommunitylabdhub.commons.exceptions.SystemException;
import it.smartcommunitylabdhub.commons.models.log.Log;
import it.smartcommunitylabdhub.commons.models.queries.SearchFilter;
import it.smartcommunitylabdhub.commons.services.LogService;
import it.smartcommunitylabdhub.core.models.entities.LogEntity;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/*
 * Searchable service for managing log
 */
public interface SearchableLogService extends LogService {
    /**
     * List all logs, with optional filters
     * @param pageable
     * @param filter
     * @return
     */
    Page<Log> searchLogs(Pageable pageable, @Nullable SearchFilter<LogEntity> filter) throws SystemException;

    /**
     * List the latest version of every log, with optional filters
     * @param project
     * @param pageable
     * @param filter
     * @return
     */
    Page<Log> searchLogsByProject(@NotNull String project, Pageable pageable, @Nullable SearchFilter<LogEntity> filter)
        throws SystemException;
}
