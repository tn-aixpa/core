package it.smartcommunitylabdhub.core.functions.service;

import it.smartcommunitylabdhub.commons.exceptions.SystemException;
import it.smartcommunitylabdhub.commons.models.function.Function;
import it.smartcommunitylabdhub.commons.models.queries.SearchFilter;
import it.smartcommunitylabdhub.commons.services.FunctionService;
import it.smartcommunitylabdhub.core.functions.persistence.FunctionEntity;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/*
 * Searchable service for managing function
 */
public interface SearchableFunctionService extends FunctionService {
    /**
     * List all functions, with optional filters
     * @param pageable
     * @param filter
     * @return
     */
    Page<Function> searchFunctions(Pageable pageable, @Nullable SearchFilter<FunctionEntity> filter)
        throws SystemException;

    /**
     * List the latest version of every function, with optional filters
     * @param pageable
     * @param filter
     * @return
     */
    Page<Function> searchLatestFunctions(Pageable pageable, @Nullable SearchFilter<FunctionEntity> filter)
        throws SystemException;
    /**
     * List all version of every function, with optional filters
     * @param project
     * @param pageable
     * @param filter
     * @return
     */
    Page<Function> searchFunctionsByProject(
        @NotNull String project,
        Pageable pageable,
        @Nullable SearchFilter<FunctionEntity> filter
    ) throws SystemException;

    /**
     * List the latest version of every function, with optional filters
     * @param project
     * @param pageable
     * @param filter
     * @return
     */
    Page<Function> searchLatestFunctionsByProject(
        @NotNull String project,
        Pageable pageable,
        @Nullable SearchFilter<FunctionEntity> filter
    ) throws SystemException;
}
