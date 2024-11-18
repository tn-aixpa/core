package it.smartcommunitylabdhub.commons.services;

import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.exceptions.SystemException;
import it.smartcommunitylabdhub.commons.models.entities.function.Function;
import it.smartcommunitylabdhub.commons.models.entities.task.Task;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindException;

/*
 * Service for managing function
 */
public interface FunctionService {
    /**
     * List all functions
     * @param pageable
     * @return
     */
    Page<Function> listFunctions(Pageable pageable) throws SystemException;

    /**
     * List the latest version of every function
     * @return
     */
    List<Function> listLatestFunctions() throws SystemException;

    /**
     * List the latest version of every function
     * @param pageable
     * @return
     */
    Page<Function> listLatestFunctions(Pageable pageable) throws SystemException;

    /**
     * List all versions of every function for a user
     * @param user
     * @return
     */
    List<Function> listFunctionsByUser(@NotNull String user) throws SystemException;

    /**
     * List all versions of every function for a project
     * @param project
     * @return
     */
    List<Function> listFunctionsByProject(@NotNull String project) throws SystemException;

    /**
     * List all versions of every function for a project
     * @param project
     * @param pageable
     * @return
     */
    Page<Function> listFunctionsByProject(@NotNull String project, Pageable pageable) throws SystemException;

    /**
     * List the latest version of every function for a project
     * @param project
     * @return
     */
    List<Function> listLatestFunctionsByProject(@NotNull String project) throws SystemException;

    /**
     * List the latest version of every function for a project
     * @param project
     * @param pageable
     * @return
     */
    Page<Function> listLatestFunctionsByProject(@NotNull String project, Pageable pageable) throws SystemException;

    /**
     * Find all versions of a given function
     * @param project
     * @param name
     * @return
     */
    List<Function> findFunctions(@NotNull String project, @NotNull String name) throws SystemException;

    /**
     * Find all versions of a given function
     * @param project
     * @param name
     * @param pageable
     * @return
     */
    Page<Function> findFunctions(@NotNull String project, @NotNull String name, Pageable pageable)
        throws SystemException;

    /**
     * Find a specific function (version) via unique ID. Returns null if not found
     * @param id
     * @return
     */
    @Nullable
    Function findFunction(@NotNull String id) throws SystemException;

    /**
     * Get a specific function (version) via unique ID. Throws exception if not found
     * @param id
     * @return
     * @throws NoSuchEntityException
     */
    Function getFunction(@NotNull String id) throws NoSuchEntityException, SystemException;

    /**
     * Get the latest version of a given function
     * @param project
     * @param name
     * @return
     * @throws NoSuchEntityException
     */
    Function getLatestFunction(@NotNull String project, @NotNull String name)
        throws NoSuchEntityException, SystemException;

    /**
     * Create a new function and store it
     * @param functionDTO
     * @return
     * @throws DuplicatedEntityException
     */
    Function createFunction(@NotNull Function functionDTO)
        throws DuplicatedEntityException, BindException, SystemException;

    /**
     * Update a specific function version
     * @param id
     * @param functionDTO
     * @return
     * @throws NoSuchEntityException
     */
    Function updateFunction(@NotNull String id, @NotNull Function functionDTO)
        throws NoSuchEntityException, BindException, IllegalArgumentException, SystemException;

    /**
     * Update a specific function version
     * @param id
     * @param functionDTO
     * @param force
     * @return
     * @throws NoSuchEntityException
     */
    Function updateFunction(@NotNull String id, @NotNull Function functionDTO, boolean force)
        throws NoSuchEntityException, SystemException;

    /**
     * Delete a specific function (version) via unique ID, with optional cascade
     * @param id
     */
    void deleteFunction(@NotNull String id, @Nullable Boolean cascade) throws SystemException;

    /**
     * Delete all versions of a given function, with cascade
     * @param project
     * @param name
     */
    void deleteFunctions(@NotNull String project, @NotNull String name) throws SystemException;

    /**
     * Delete all functions for a given project, with cascade.
     * @param project
     */
    void deleteFunctionsByProject(@NotNull String project) throws SystemException;

    /**
     * List all tasks for a given function
     * @param function
     * @return
     */
    List<Task> getTasksByFunctionId(@NotNull String functionId) throws SystemException;
}
