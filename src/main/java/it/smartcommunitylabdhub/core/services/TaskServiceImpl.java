package it.smartcommunitylabdhub.core.services;

import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecRegistry;
import it.smartcommunitylabdhub.core.exceptions.CoreException;
import it.smartcommunitylabdhub.core.exceptions.CustomException;
import it.smartcommunitylabdhub.core.models.accessors.utils.TaskAccessor;
import it.smartcommunitylabdhub.core.models.accessors.utils.TaskUtils;
import it.smartcommunitylabdhub.core.models.base.interfaces.Spec;
import it.smartcommunitylabdhub.core.models.builders.task.TaskDTOBuilder;
import it.smartcommunitylabdhub.core.models.builders.task.TaskEntityBuilder;
import it.smartcommunitylabdhub.core.models.entities.task.Task;
import it.smartcommunitylabdhub.core.models.entities.task.TaskEntity;
import it.smartcommunitylabdhub.core.models.entities.task.specs.TaskBaseSpec;
import it.smartcommunitylabdhub.core.models.enums.State;
import it.smartcommunitylabdhub.core.models.queries.filters.abstracts.AbstractSpecificationService;
import it.smartcommunitylabdhub.core.models.queries.filters.entities.TaskEntityFilter;
import it.smartcommunitylabdhub.core.repositories.RunRepository;
import it.smartcommunitylabdhub.core.repositories.TaskRepository;
import it.smartcommunitylabdhub.core.services.interfaces.TaskService;
import it.smartcommunitylabdhub.core.utils.ErrorList;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class TaskServiceImpl extends AbstractSpecificationService<TaskEntity, TaskEntityFilter>
        implements TaskService {

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    RunRepository runRepository;

    @Autowired
    TaskDTOBuilder taskDTOBuilder;

    @Autowired
    TaskEntityBuilder taskEntityBuilder;

    @Autowired
    TaskEntityFilter taskEntityFilter;

    @Autowired
    SpecRegistry<? extends Spec> specRegistry;

    @Override
    public Page<Task> getTasks(Map<String, String> filter, Pageable pageable) {
        try {
            taskEntityFilter.setFunction(filter.get("function"));
            taskEntityFilter.setKind(filter.get("kind"));
            taskEntityFilter.setCreatedDate(filter.get("created"));
            Optional<State> stateOptional = Stream.of(State.values())
                    .filter(state -> state.name().equals(filter.get("state")))
                    .findAny();
            taskEntityFilter.setState(stateOptional.map(Enum::name).orElse(null));

            Specification<TaskEntity> specification = createSpecification(filter, taskEntityFilter);

            Page<TaskEntity> taskPage = this.taskRepository.findAll(specification, pageable);

            return new PageImpl<>(
                    taskPage.getContent()
                            .stream()
                            .map(task -> taskDTOBuilder.build(task))
                            .collect(Collectors.toList()),
                    pageable,
                    taskPage.getContent().size());

        } catch (CustomException e) {
            throw new CoreException("InternalServerError", e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Task getTask(String uuid) {
        return taskRepository.findById(uuid).map(task -> taskDTOBuilder.build(task))
                .orElseThrow(() -> new CoreException("TaskNotFound",
                        "The Task you are searching for does not exist.",
                        HttpStatus.NOT_FOUND));
    }

    @Override
    public List<Task> getTasksByFunction(String function) {
        return taskRepository.findByFunction(function).stream()
                .map(taskDTOBuilder::build).toList();
    }

    @Override
    public boolean deleteTask(String uuid, Boolean cascade) {
        try {
            if (this.taskRepository.existsById(uuid)) {
                if (cascade) {
                    this.runRepository.deleteByTaskId(uuid);
                }
                this.taskRepository.deleteById(uuid);
                return true;
            }
            throw new CoreException(
                    ErrorList.TASK_NOT_FOUND.getValue(),
                    ErrorList.TASK_NOT_FOUND.getReason(),
                    HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            throw new CoreException(
                    ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                    "Cannot delete task",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Task createTask(Task taskDTO) {
        if (taskDTO.getId() != null && taskRepository.existsById(taskDTO.getId())) {
            throw new CoreException("DuplicateTaskId", "Cannot create the task",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        TaskBaseSpec taskSpec = specRegistry.createSpec(
                taskDTO.getKind(),
                EntityName.TASK,
                taskDTO.getSpec());

        TaskAccessor taskAccessor = TaskUtils.parseTask(taskSpec.getFunction());
        if (!taskDTO.getProject().equals(taskAccessor.getProject())) {
            throw new CoreException("Task string Project and associated Project does not match",
                    "Cannot create the task", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Optional<TaskEntity> savedTask = Optional.ofNullable(taskDTO).map(taskEntityBuilder::build)
                .map(this.taskRepository::saveAndFlush);

        return savedTask.map(task -> taskDTOBuilder.build(task)).orElseThrow(
                () -> new CoreException("InternalServerError", "Error saving task",
                        HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Override
    public Task updateTask(Task taskDTO, String uuid) {
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
            this.taskRepository.saveAndFlush(taskUpdated);

            return taskDTOBuilder.build(taskUpdated);

        } catch (CustomException e) {
            throw new CoreException("InternalServerError", e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
