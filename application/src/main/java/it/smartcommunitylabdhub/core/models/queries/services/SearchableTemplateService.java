package it.smartcommunitylabdhub.core.models.queries.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import it.smartcommunitylabdhub.commons.exceptions.SystemException;
import it.smartcommunitylabdhub.commons.models.function.Function;
import it.smartcommunitylabdhub.core.models.queries.filters.entities.FunctionEntityFilter;
import jakarta.annotation.Nullable;

/*
 * Searchable service for managing function
 */
public interface SearchableTemplateService {
    /**
     * List all functions, with optional filters
     * @param pageable
     * @param filter
     * @return
     */
    Page<Function> searchFunctions(Pageable pageable, @Nullable FunctionEntityFilter filter)
        throws SystemException;

}
