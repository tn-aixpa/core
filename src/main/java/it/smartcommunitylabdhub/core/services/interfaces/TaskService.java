package it.smartcommunitylabdhub.core.services.interfaces;

import it.smartcommunitylabdhub.core.models.entities.task.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface TaskService {

    Page<Task> getTasks(Map<String, String> filter, Pageable pageable);

    Task getTask(String uuid);

    List<Task> getTasksByFunction(String function);

    boolean deleteTask(String uuid, Boolean cascade);

    Task createTask(Task TaskDTO);

    Task updateTask(Task TaskDTO, String uuid);
}
