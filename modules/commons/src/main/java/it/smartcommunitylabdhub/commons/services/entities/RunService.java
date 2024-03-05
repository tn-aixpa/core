package it.smartcommunitylabdhub.commons.services.entities;

import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import jakarta.validation.Valid;
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
    Run buildRun(@NotNull @Valid Run dto);

    Run execRun(@NotNull @Valid Run dto);

    /*
     * Tasks
     */
    List<Run> getRunsByTaskId(@NotNull String taskId);
    void deleteRunsByTaskId(@NotNull String taskId);

    /**
     * List all runs
     * @param pageable
     * @return
     */
    Page<Run> listRuns(Pageable pageable);

    /**
     * List all runs for a given project
     * @param project
     * @param pageable
     * @return
     */
    Page<Run> listRunsByProject(@NotNull String project, Pageable pageable);

    /**
     * Find a specific run via unique ID. Returns null if not found
     * @param id
     * @return
     */
    @Nullable
    Run findRun(@NotNull String id);

    /**
     * Get a specific run via unique ID. Throws exception if not found
     * @param id
     * @return
     * @throws NoSuchEntityException
     */
    Run getRun(@NotNull String id) throws NoSuchEntityException;

    /**
     * Create a new run and store it
     * @param runDTO
     * @return
     * @throws DuplicatedEntityException
     */
    Run createRun(@NotNull Run runDTO) throws DuplicatedEntityException;

    /**
     * Update a specific run
     * @param id
     * @param runDTO
     * @return
     * @throws NoSuchEntityException
     */
    Run updateRun(@NotNull String id, @NotNull Run runDTO) throws NoSuchEntityException;

    /**
     * Delete a specific run via unique ID, with optional cascade
     * @param id
     * @param cascade
     */
    void deleteRun(@NotNull String id, @Nullable Boolean cascade);
}
