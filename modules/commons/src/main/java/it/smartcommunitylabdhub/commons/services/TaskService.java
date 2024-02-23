package it.smartcommunitylabdhub.commons.services;

import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.models.entities.task.Task;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

public interface TaskService {
    Page<Task> getTasks(Map<String, String> filter, Pageable pageable);

    @Nullable
    Task findTask(String id);

    Task getTask(String id) throws NoSuchEntityException;

    Task createTask(@NotNull @Valid Task dto);

    Task updateTask(@NotNull String id, @NotNull @Valid Task dto) throws NoSuchEntityException;

    boolean deleteTask(String uuid, Boolean cascade);

    /*
     * Functions
     */
    List<Task> getTasksByFunction(@NotNull String functionId);
    void deleteTasksByFunction(@NotNull String functionId);
}
