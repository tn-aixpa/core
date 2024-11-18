package it.smartcommunitylabdhub.commons.services.entities;

import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.exceptions.SystemException;
import it.smartcommunitylabdhub.commons.models.entities.task.Task;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindException;

/*
 * Service for managing tasks
 */
public interface TaskService {
    /**
     * List all tasks
     * @param pageable
     * @return
     */
    Page<Task> listTasks(Pageable pageable) throws SystemException;

    /**
     * List all tasks for a given user
     * @param user
     * @return
     */
    List<Task> listTasksByUser(@NotNull String user) throws SystemException;

    /**
     * List all tasks for a given project
     * @param project
     * @return
     */
    List<Task> listTasksByProject(@NotNull String project) throws SystemException;

    /**
     * List all tasks for a given project
     * @param project
     * @param pageable
     * @return
     */
    Page<Task> listTasksByProject(@NotNull String project, Pageable pageable) throws SystemException;

    /**
     * Find a specific task  via unique ID. Returns null if not found
     * @param id
     * @return
     */
    @Nullable
    Task findTask(@NotNull String id) throws SystemException;

    /**
     * Get a specific task via unique ID. Throws exception if not found
     * @param id
     * @return
     * @throws NoSuchEntityException
     */
    Task getTask(@NotNull String id) throws NoSuchEntityException, SystemException;

    /**
     * Create a new task and store it
     * @param taskDTO
     * @return
     * @throws DuplicatedEntityException
     */
    Task createTask(@NotNull Task taskDTO)
        throws DuplicatedEntityException, BindException, IllegalArgumentException, SystemException;

    /**
     * Update a specific task
     * @param id
     * @param taskDTO
     * @return
     * @throws NoSuchEntityException
     */
    Task updateTask(@NotNull String id, @NotNull Task taskDTO)
        throws NoSuchEntityException, BindException, IllegalArgumentException, SystemException;

    /**
     * Delete a specific task via unique ID, with optional cascade
     * @param id
     * @param cascade
     */
    void deleteTask(@NotNull String id, @Nullable Boolean cascade) throws SystemException;
}
