package it.smartcommunitylabdhub.commons.services.entities;

import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.models.entities.function.Function;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

/*
 * Service for managing function
 */
public interface FunctionService {
    /**
     * List all functions
     * @param pageable
     * @return
     */
    Page<Function> listFunctions(Pageable pageable);

    /**
     * List all versions of every function for a project
     * @param project
     * @return
     */
    List<Function> listFunctionsByProject(@NotNull String project);

    /**
     * List all versions of every function for a project
     * @param project
     * @param pageable
     * @return
     */
    Page<Function> listFunctionsByProject(@NotNull String project, Pageable pageable);

    /**
     * List the latest version of every function for a project
     * @param project
     * @return
     */
    List<Function> listLatestFunctionsByProject(@NotNull String project);

    /**
     * List the latest version of every function for a project
     * @param project
     * @param pageable
     * @return
     */
    Page<Function> listLatestFunctionsByProject(@NotNull String project, Pageable pageable);

    /**
     * Find all versions of a given function
     * @param project
     * @param name
     * @return
     */
    List<Function> findFunctions(@NotNull String project, @NotNull String name);

    /**
     * Find all versions of a given function
     * @param project
     * @param name
     * @param pageable
     * @return
     */
    Page<Function> findFunctions(@NotNull String project, @NotNull String name, Pageable pageable);

    /**
     * Find a specific function (version) via unique ID. Returns null if not found
     * @param id
     * @return
     */
    @Nullable
    Function findFunction(@NotNull String id);

    /**
     * Get a specific function (version) via unique ID. Throws exception if not found
     * @param id
     * @return
     * @throws NoSuchEntityException
     */
    Function getFunction(@NotNull String id) throws NoSuchEntityException;

    /**
     * Get the latest version of a given function
     * @param project
     * @param name
     * @return
     * @throws NoSuchEntityException
     */
    Function getLatestFunction(@NotNull String project, @NotNull String name) throws NoSuchEntityException;

    /**
     * Create a new function and store it
     * @param functionDTO
     * @return
     * @throws DuplicatedEntityException
     */
    Function createFunction(@NotNull Function functionDTO) throws DuplicatedEntityException;

    /**
     * Update a specific function version
     * @param id
     * @param functionDTO
     * @return
     * @throws NoSuchEntityException
     */
    Function updateFunction(@NotNull String id, @NotNull Function functionDTO) throws NoSuchEntityException;

    /**
     * Delete a specific function (version) via unique ID, with optional cascade
     * @param id
     */
    void deleteFunction(@NotNull String id, @Nullable Boolean cascade);

    /**
     * Delete all versions of a given function, with cascade
     * @param project
     * @param name
     */
    void deleteFunctions(@NotNull String project, @NotNull String name);

    /**
     * Delete all functions for a given project, with cascade.
     * @param project
     */
    void deleteFunctionsByProject(@NotNull String project);
}
