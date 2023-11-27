package it.smartcommunitylabdhub.core.services.interfaces;

import it.smartcommunitylabdhub.core.models.entities.task.XTask;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TaskService {

    List<XTask> getTasks(Pageable pageable);

    XTask getTask(String uuid);

    boolean deleteTask(String uuid);

    XTask createTask(XTask TaskDTO);

    XTask updateTask(XTask TaskDTO, String uuid);
}
