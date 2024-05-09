package it.smartcommunitylabdhub.commons.services.entities;

import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.exceptions.SystemException;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

/*
 * Service for managing runs
 */
public interface RunService {
    /*
     * Execution
     * TODO move to manager
     */
    // Run buildRun(@NotNull @Valid Run dto) throws SystemException;

    // Run execRun(@NotNull @Valid Run dto) throws SystemException;

    /*
     * Tasks
     */
    List<Run> getRunsByTaskId(@NotNull String taskId) throws SystemException;

    void deleteRunsByTaskId(@NotNull String taskId) throws SystemException;

    /**
     * List all runs
     *
     * @param pageable
     * @return
     */
    Page<Run> listRuns(Pageable pageable) throws SystemException;

    /**
     * List all runs for a given user
     *
     * @param user
     * @return
     */
    List<Run> listRunsByUser(@NotNull String user) throws SystemException;

    /**
     * List all runs for a given project
     *
     * @param project
     * @return
     */
    List<Run> listRunsByProject(@NotNull String project) throws SystemException;

    /**
     * List all runs for a given project
     *
     * @param project
     * @param pageable
     * @return
     */
    Page<Run> listRunsByProject(@NotNull String project, Pageable pageable) throws SystemException;

    /**
     * Find a specific run via unique ID. Returns null if not found
     *
     * @param id
     * @return
     */
    @Nullable
    Run findRun(@NotNull String id) throws SystemException;

    /**
     * Get a specific run via unique ID. Throws exception if not found
     *
     * @param id
     * @return
     * @throws NoSuchEntityException
     */
    Run getRun(@NotNull String id) throws NoSuchEntityException, SystemException;

    /**
     * Create a new run and store it
     *
     * @param runDTO
     * @return
     * @throws DuplicatedEntityException
     */
    Run createRun(@NotNull Run runDTO) throws DuplicatedEntityException, NoSuchEntityException, SystemException;

    /**
     * Update a specific run
     *
     * @param id
     * @param runDTO
     * @return
     * @throws NoSuchEntityException
     */
    Run updateRun(@NotNull String id, @NotNull Run runDTO) throws NoSuchEntityException, SystemException;

    /**
     * Delete a specific run via unique ID, with optional cascade
     *
     * @param id
     * @param cascade
     */
    void deleteRun(@NotNull String id, @Nullable Boolean cascade) throws SystemException;
}
