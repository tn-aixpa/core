package it.smartcommunitylabdhub.core.services;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecEntity;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecRegistry;
import it.smartcommunitylabdhub.core.exceptions.CoreException;
import it.smartcommunitylabdhub.core.exceptions.CustomException;
import it.smartcommunitylabdhub.core.models.accessors.utils.TaskAccessor;
import it.smartcommunitylabdhub.core.models.accessors.utils.TaskUtils;
import it.smartcommunitylabdhub.core.models.base.interfaces.Spec;
import it.smartcommunitylabdhub.core.models.builders.task.TaskDTOBuilder;
import it.smartcommunitylabdhub.core.models.builders.task.TaskEntityBuilder;
import it.smartcommunitylabdhub.core.models.entities.task.TaskEntity;
import it.smartcommunitylabdhub.core.models.entities.task.XTask;
import it.smartcommunitylabdhub.core.models.entities.task.specs.TaskBaseSpec;
import it.smartcommunitylabdhub.core.repositories.TaskRepository;
import it.smartcommunitylabdhub.core.services.interfaces.TaskService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    TaskDTOBuilder taskDTOBuilder;

    @Autowired
    TaskEntityBuilder taskEntityBuilder;

    @Autowired
    SpecRegistry<? extends Spec> specRegistry;

    @Override
    public List<XTask> getTasks(Pageable pageable) {
        try {
            Page<TaskEntity> TaskPage = this.taskRepository.findAll(pageable);
            return TaskPage.getContent().stream().map(task -> taskDTOBuilder.build(task))
                    .collect(Collectors.toList());

        } catch (CustomException e) {
            throw new CoreException("InternalServerError", e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public XTask getTask(String uuid) {
        return taskRepository.findById(uuid).map(task -> taskDTOBuilder.build(task))
                .orElseThrow(() -> new CoreException("TaskNotFound",
                        "The Task you are searching for does not exist.",
                        HttpStatus.NOT_FOUND));
    }

    @Override
    public boolean deleteTask(String uuid) {
        try {
            this.taskRepository.deleteById(uuid);
            return true;
        } catch (Exception e) {
            throw new CoreException("InternalServerError", "cannot delete artifact",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public XTask createTask(XTask taskDTO) {
        if (taskDTO.getId() != null && taskRepository.existsById(taskDTO.getId())) {
            throw new CoreException("DuplicateTaskId", "Cannot create the task",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        TaskBaseSpec<?> taskSpec = specRegistry.createSpec(
                taskDTO.getKind(),
                SpecEntity.TASK,
                taskDTO.getSpec());

        TaskAccessor taskAccessor = TaskUtils.parseTask(taskSpec.getFunction());
        if (!taskDTO.getProject().equals(taskAccessor.getProject())) {
            throw new CoreException("Task string Project and associated Project does not match",
                    "Cannot create the task", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Optional<TaskEntity> savedTask = Optional.ofNullable(taskDTO).map(taskEntityBuilder::build)
                .map(this.taskRepository::save);

        return savedTask.map(task -> taskDTOBuilder.build(task)).orElseThrow(
                () -> new CoreException("InternalServerError", "Error saving task",
                        HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Override
    public XTask updateTask(XTask taskDTO, String uuid) {
        if (!taskDTO.getId().equals(uuid)) {
            throw new CoreException("TaskNotMatch",
                    "Trying to update a task with an uuid different from the one passed in the request.",
                    HttpStatus.NOT_FOUND);
        }

        final TaskEntity task = taskRepository.findById(uuid).orElse(null);
        if (task == null) {
            throw new CoreException("TaskNotFound",
                    "The task you are searching for does not exist.",
                    HttpStatus.NOT_FOUND);
        }

        try {

            final TaskEntity taskUpdated = taskEntityBuilder.update(task, taskDTO);
            this.taskRepository.save(taskUpdated);

            return taskDTOBuilder.build(taskUpdated);

        } catch (CustomException e) {
            throw new CoreException("InternalServerError", e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
