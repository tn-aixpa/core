package it.smartcommunitylabdhub.core.services.context;

import it.smartcommunitylabdhub.core.exceptions.CoreException;
import it.smartcommunitylabdhub.core.exceptions.CustomException;
import it.smartcommunitylabdhub.core.models.builders.task.TaskDTOBuilder;
import it.smartcommunitylabdhub.core.models.builders.task.TaskEntityBuilder;
import it.smartcommunitylabdhub.core.models.entities.task.Task;
import it.smartcommunitylabdhub.core.models.entities.task.TaskEntity;
import it.smartcommunitylabdhub.core.models.filters.entities.TaskEntityFilter;
import it.smartcommunitylabdhub.core.repositories.RunRepository;
import it.smartcommunitylabdhub.core.repositories.TaskRepository;
import it.smartcommunitylabdhub.core.services.context.interfaces.TaskContextService;
import it.smartcommunitylabdhub.core.utils.ErrorList;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskContextServiceImpl
        extends ContextService<TaskEntity, TaskEntityFilter>
        implements TaskContextService {

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    TaskDTOBuilder taskDTOBuilder;

    @Autowired
    TaskEntityBuilder taskEntityBuilder;

    @Autowired
    TaskEntityFilter taskEntityFilter;

    @Autowired
    RunRepository runRepository;

    @Override
    public Task createTask(String projectName, Task taskDTO) {
        try {
            // Check that project context is the same as the project passed to the
            // taskDTO
            if (!projectName.equals(taskDTO.getProject())) {
                throw new CustomException("Project Context and Task Project does not match",
                        null);
            }

            // Check project context
            checkContext(taskDTO.getProject());

            // Check if task already exist if exist throw exception otherwise create a
            // new one
            TaskEntity task = (TaskEntity) Optional.ofNullable(taskDTO.getId())
                    .flatMap(id -> taskRepository.findById(id)
                            .map(a -> {
                                throw new CoreException(
                                        ErrorList.DUPLICATE_TASK.getValue(),
                                        ErrorList.DUPLICATE_TASK.getReason(),
                                        HttpStatus.INTERNAL_SERVER_ERROR);
                            }))
                    .orElseGet(() -> {
                        // Build a task and store it in the database
                        TaskEntity newTask = taskEntityBuilder.build(taskDTO);
                        return taskRepository.saveAndFlush(newTask);
                    });

            // Return task DTO
            return taskDTOBuilder.build(task);

        } catch (CustomException e) {
            throw new CoreException(
                    ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public Page<Task> getAllTasksByProjectName(Map<String, String> filters, String projectName, Pageable pageable) {
        try {
            checkContext(projectName);

            taskEntityFilter.setFunction(filters.get("function"));

            Specification<TaskEntity> specification = createSpecification(filters, taskEntityFilter);

            Page<TaskEntity> taskPage = taskRepository.findAll(
                    Specification.where(specification).and((root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(root.get("project"), projectName)), pageable);
            
            return new PageImpl<>(
                    taskPage.getContent()
                            .stream()
                            .map(task -> taskDTOBuilder.build(task))
                            .collect(Collectors.toList()),
                    pageable, taskPage.getContent().size()
            );
        } catch (CustomException e) {
            throw new CoreException(
                    ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public Task getByProjectAndTaskUuid(String projectName,
                                        String uuid) {
        try {
            // Check project context
            checkContext(projectName);

            return this.taskRepository
                    .findByProjectAndId(projectName, uuid).map(
                            task -> taskDTOBuilder.build(task))
                    .orElseThrow(
                            () -> new CustomException(ErrorList.TASK_NOT_FOUND.getReason(),
                                    null));

        } catch (CustomException e) {
            throw new CoreException(
                    ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public Task updateTask(String projectName,
                           String uuid,
                           Task taskDTO) {

        try {
            // Check that project context is the same as the project passed to the
            // taskDTO
            if (!projectName.equals(taskDTO.getProject())) {
                throw new CustomException("Project Context and Task Project does not match",
                        null);
            }
            if (!uuid.equals(taskDTO.getId())) {
                throw new CustomException(
                        "Trying to update a task with an ID different from the one passed in the request.",
                        null);
            }
            // Check project context
            checkContext(taskDTO.getProject());

            TaskEntity task = this.taskRepository.findById(taskDTO.getId()).map(
                            a -> // Update the existing task version
                                    taskEntityBuilder.update(a, taskDTO))
                    .orElseThrow(
                            () -> new CoreException(
                                    ErrorList.TASK_NOT_FOUND.getValue(),
                                    ErrorList.TASK_NOT_FOUND.getReason(),
                                    HttpStatus.INTERNAL_SERVER_ERROR
                            ));

            // Return task DTO
            return taskDTOBuilder.build(task);

        } catch (CustomException e) {
            throw new CoreException(
                    ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public Boolean deleteSpecificTaskVersion(String projectName, String uuid) {
        try {
            if (this.taskRepository.existsByProjectAndId(projectName, uuid)) {

                // Delete Task
                this.taskRepository.deleteByProjectAndId(projectName, uuid);

                // Delete Run
                this.runRepository.deleteByTaskId(uuid);
                return true;
            }
            throw new CoreException(
                    ErrorList.TASK_NOT_FOUND.getValue(),
                    ErrorList.TASK_NOT_FOUND.getReason(),
                    HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            throw new CoreException(
                    ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                    "cannot delete task",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
