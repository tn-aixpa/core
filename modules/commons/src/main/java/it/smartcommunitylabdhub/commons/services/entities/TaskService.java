package it.smartcommunitylabdhub.commons.services.entities;

import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.models.entities.task.Task;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

/*
 * Service for managing tasks
 */
public interface TaskService {
    /**
     * List all tasks for a given function
     * @param function
     * @return
     */
    List<Task> getTasksByFunctionId(@NotNull String functionId);

    /**
     * List all tasks
     * @param pageable
     * @return
     */
    Page<Task> listTasks(Pageable pageable);

    /**
     * List all tasks for a given user
     * @param user
     * @return
     */
    List<Task> listTasksByUser(@NotNull String user);

    /**
     * List all tasks for a given project
     * @param project
     * @return
     */
    List<Task> listTasksByProject(@NotNull String project);

    /**
     * List all tasks for a given project
     * @param project
     * @param pageable
     * @return
     */
    Page<Task> listTasksByProject(@NotNull String project, Pageable pageable);

    /**
     * Find a specific task  via unique ID. Returns null if not found
     * @param id
     * @return
     */
    @Nullable
    Task findTask(@NotNull String id);

    /**
     * Get a specific task via unique ID. Throws exception if not found
     * @param id
     * @return
     * @throws NoSuchEntityException
     */
    Task getTask(@NotNull String id) throws NoSuchEntityException;

    /**
     * Create a new task and store it
     * @param taskDTO
     * @return
     * @throws DuplicatedEntityException
     */
    Task createTask(@NotNull Task taskDTO) throws DuplicatedEntityException;

    /**
     * Update a specific task
     * @param id
     * @param taskDTO
     * @return
     * @throws NoSuchEntityException
     */
    Task updateTask(@NotNull String id, @NotNull Task taskDTO) throws NoSuchEntityException;

    /**
     * Delete a specific task via unique ID, with optional cascade
     * @param id
     * @param cascade
     */
    void deleteTask(@NotNull String id, @Nullable Boolean cascade);

    /**
     * Delete all tasks for a given function, with cascade.
     * @param function
     */
    void deleteTasksByFunctionId(@NotNull String functionId);
}
