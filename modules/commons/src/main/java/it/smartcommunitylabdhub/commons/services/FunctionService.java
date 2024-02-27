package it.smartcommunitylabdhub.commons.services;

import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.models.entities.function.Function;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.queries.SearchFilter;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

/*
 * Service for managing function
 */
public interface FunctionService<T> {
    @Deprecated
    List<Run> getFunctionRuns(@NotNull String id) throws NoSuchEntityException;

    Page<Function> listFunctions(Pageable pageable, @Nullable SearchFilter<T> filter);
    Page<Function> listLatestFunctionsByProject(
        @NotNull String project,
        Pageable pageable,
        @Nullable SearchFilter<T> filter
    );

    List<Function> findFunctions(@NotNull String project, @NotNull String name);
    Page<Function> findFunctions(@NotNull String project, @NotNull String name, Pageable pageable);

    Function getFunction(@NotNull String id) throws NoSuchEntityException;
    Function getLatestFunction(@NotNull String project, @NotNull String name) throws NoSuchEntityException;

    Function createFunction(@NotNull Function functionDTO) throws DuplicatedEntityException;
    Function updateFunction(@NotNull String id, @NotNull Function functionDTO) throws NoSuchEntityException;
    void deleteFunction(@NotNull String id, @Nullable Boolean cascade);
    void deleteFunctions(@NotNull String project, @NotNull String name, @Nullable Boolean cascade);
}
