package it.smartcommunitylabdhub.core.models.queries.services;

import it.smartcommunitylabdhub.commons.exceptions.SystemException;
import it.smartcommunitylabdhub.commons.models.queries.SearchFilter;
import it.smartcommunitylabdhub.commons.models.task.Task;
import it.smartcommunitylabdhub.commons.services.TaskService;
import it.smartcommunitylabdhub.core.models.entities.TaskEntity;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/*
 * Searchable service for managing task
 */
public interface SearchableTaskService extends TaskService {
    /**
     * List all tasks, with optional filters
     * @param pageable
     * @param filter
     * @return
     */
    Page<Task> searchTasks(Pageable pageable, @Nullable SearchFilter<TaskEntity> filter) throws SystemException;

    /**
     * List the latest version of every task, with optional filters
     * @param project
     * @param pageable
     * @param filter
     * @return
     */
    Page<Task> searchTasksByProject(
        @NotNull String project,
        Pageable pageable,
        @Nullable SearchFilter<TaskEntity> filter
    ) throws SystemException;
}
