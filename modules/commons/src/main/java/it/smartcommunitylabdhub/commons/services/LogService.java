package it.smartcommunitylabdhub.commons.services;

import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.exceptions.SystemException;
import it.smartcommunitylabdhub.commons.models.entities.log.Log;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindException;

/*
 * Service for managing logs
 */
public interface LogService {
    /**
     * List all logs
     * @param pageable
     * @return
     */
    Page<Log> listLogs(Pageable pageable) throws SystemException;

    /**
     * List all versions of every log for a user
     * @param user
     * @return
     */
    List<Log> listLogsByUser(@NotNull String user) throws SystemException;

    /**
     * List all versions of every log for a project
     * @param project
     * @return
     */
    List<Log> listLogsByProject(@NotNull String project) throws SystemException;

    /**
     * List all versions of every log for a project
     * @param project
     * @param pageable
     * @return
     */
    Page<Log> listLogsByProject(@NotNull String project, Pageable pageable) throws SystemException;

    /**
     * List all logs for a given run
     * @param run
     * @return
     */
    List<Log> getLogsByRunId(@NotNull String runId) throws SystemException;

    /**
     * Find a specific log (version) via unique ID. Returns null if not found
     * @param id
     * @return
     */
    @Nullable
    Log findLog(@NotNull String id) throws SystemException;

    /**
     * Get a specific log (version) via unique ID. Throws exception if not found
     * @param id
     * @return
     * @throws NoSuchEntityException
     */
    Log getLog(@NotNull String id) throws NoSuchEntityException, SystemException;

    /**
     * Create a new log and store it
     * @param logDTO
     * @return
     * @throws DuplicatedEntityException
     */
    Log createLog(@NotNull Log logDTO)
        throws DuplicatedEntityException, BindException, IllegalArgumentException, SystemException;

    /**
     * Update a specific log version
     * @param id
     * @param logDTO
     * @return
     * @throws NoSuchEntityException
     */
    Log updateLog(@NotNull String id, @NotNull Log logDTO)
        throws NoSuchEntityException, BindException, IllegalArgumentException, SystemException;

    /**
     * Delete a specific log (version) via unique ID
     * @param id
     */
    void deleteLog(@NotNull String id) throws SystemException;

    /**
     * Delete all logs for a given project, with cascade.
     * @param project
     */
    void deleteLogsByProject(@NotNull String project) throws SystemException;

    /**
     * Delete all logs for a given run, with cascade.
     * @param run
     * @param entity
     */
    void deleteLogsByRunId(@NotNull String runId) throws SystemException;
}
