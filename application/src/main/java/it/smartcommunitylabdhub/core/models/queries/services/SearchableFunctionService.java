package it.smartcommunitylabdhub.core.models.queries.services;

import it.smartcommunitylabdhub.commons.models.entities.function.Function;
import it.smartcommunitylabdhub.commons.models.queries.SearchFilter;
import it.smartcommunitylabdhub.commons.services.entities.FunctionService;
import it.smartcommunitylabdhub.core.models.entities.function.FunctionEntity;
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
    Page<Function> searchFunctions(Pageable pageable, @Nullable SearchFilter<FunctionEntity> filter);

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
    );
}
