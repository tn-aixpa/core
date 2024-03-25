package it.smartcommunitylabdhub.core.services;

import it.smartcommunitylabdhub.commons.accessors.spec.TaskSpecAccessor;
import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.models.entities.function.Function;
import it.smartcommunitylabdhub.commons.models.entities.project.Project;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.entities.task.Task;
import it.smartcommunitylabdhub.commons.models.entities.task.TaskBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.queries.SearchFilter;
import it.smartcommunitylabdhub.commons.models.specs.Spec;
import it.smartcommunitylabdhub.commons.models.utils.TaskUtils;
import it.smartcommunitylabdhub.commons.services.SpecRegistry;
import it.smartcommunitylabdhub.commons.services.entities.RunService;
import it.smartcommunitylabdhub.core.models.entities.function.FunctionEntity;
import it.smartcommunitylabdhub.core.models.entities.project.ProjectEntity;
import it.smartcommunitylabdhub.core.models.entities.task.TaskEntity;
import it.smartcommunitylabdhub.core.models.entities.task.TaskEntity_;
import it.smartcommunitylabdhub.core.models.events.EntityAction;
import it.smartcommunitylabdhub.core.models.events.EntityOperation;
import it.smartcommunitylabdhub.core.models.queries.services.SearchableTaskService;
import it.smartcommunitylabdhub.core.models.queries.specifications.CommonSpecification;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Transactional
@Slf4j
public class TaskServiceImpl implements SearchableTaskService {

    @Autowired
    private EntityService<Task, TaskEntity> entityService;

    @Autowired
    private EntityService<Function, FunctionEntity> functionEntityService;

    @Autowired
    private EntityService<Project, ProjectEntity> projectService;

    @Autowired
    private RunService runService;

    @Autowired
    SpecRegistry specRegistry;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public Page<Task> listTasks(Pageable pageable) {
        log.debug("list tasks page {}", pageable);

        return entityService.list(pageable);
    }

    @Override
    public Page<Task> searchTasks(Pageable pageable, @Nullable SearchFilter<TaskEntity> filter) {
        log.debug("list tasks page {}, filter {}", pageable, String.valueOf(filter));

        Specification<TaskEntity> specification = filter != null ? filter.toSpecification() : null;
        if (specification != null) {
            return entityService.search(specification, pageable);
        } else {
            return entityService.list(pageable);
        }
    }

    @Override
    public Page<Task> listTasksByProject(@NotNull String project, Pageable pageable) {
        log.debug("list tasks for project {} page {}", project, pageable);
        Specification<TaskEntity> specification = Specification.allOf(CommonSpecification.projectEquals(project));

        return entityService.search(specification, pageable);
    }

    @Override
    public Page<Task> searchTasksByProject(
        @NotNull String project,
        Pageable pageable,
        @Nullable SearchFilter<TaskEntity> filter
    ) {
        log.debug("list tasks for project {} with {} page {}", project, String.valueOf(filter), pageable);
        Specification<TaskEntity> filterSpecification = filter != null ? filter.toSpecification() : null;
        Specification<TaskEntity> specification = Specification.allOf(
            CommonSpecification.projectEquals(project),
            filterSpecification
        );

        return entityService.search(specification, pageable);
    }

    @Override
    public List<Task> getTasksByFunctionId(@NotNull String functionId) {
        log.debug("list tasks for function {}", functionId);

        Function function = functionEntityService.find(functionId);
        if (function == null) {
            return Collections.emptyList();
        }

        //define a spec for tasks building function path
        Specification<TaskEntity> where = Specification.allOf(
            CommonSpecification.projectEquals(function.getProject()),
            createFunctionSpecification(TaskUtils.buildFunctionString(function))
        );

        //fetch all tasks ordered by kind ASC
        Specification<TaskEntity> specification = (root, query, builder) -> {
            query.orderBy(builder.asc(root.get(TaskEntity_.KIND)));
            return where.toPredicate(root, query, builder);
        };

        return entityService.searchAll(specification);
    }

    @Override
    public Task findTask(@NotNull String id) {
        log.debug("find task with id {}", String.valueOf(id));

        return entityService.find(id);
    }

    @Override
    public Task getTask(@NotNull String id) throws NoSuchEntityException {
        log.debug("get task with id {}", String.valueOf(id));

        try {
            return entityService.get(id);
        } catch (NoSuchEntityException e) {
            throw new NoSuchEntityException(EntityName.TASK.toString());
        }
    }

    @Override
    public Task createTask(@NotNull Task dto) throws DuplicatedEntityException {
        log.debug("create task");

        //validate project
        String projectId = dto.getProject();
        if (!StringUtils.hasText(projectId) || projectService.find(projectId) == null) {
            throw new IllegalArgumentException("invalid or missing project");
        }

        try {
            //check if the same task already exists for the function
            TaskBaseSpec taskSpec = new TaskBaseSpec();
            taskSpec.configure(dto.getSpec());

            // Parse and export Spec
            Spec spec = specRegistry.createSpec(dto.getKind(), EntityName.TASK, dto.getSpec());
            if (spec == null) {
                throw new IllegalArgumentException("invalid kind");
            }

            //TODO validate spec via validator
            //update spec as exported
            dto.setSpec(spec.toMap());

            String function = taskSpec.getFunction();
            if (!StringUtils.hasText(function)) {
                throw new IllegalArgumentException("missing function");
            }

            TaskSpecAccessor taskSpecAccessor = TaskUtils.parseFunction(function);
            if (!StringUtils.hasText(taskSpecAccessor.getProject())) {
                throw new IllegalArgumentException("spec: missing project");
            }

            //check project match
            if (dto.getProject() != null && !dto.getProject().equals(taskSpecAccessor.getProject())) {
                throw new IllegalArgumentException("project mismatch");
            }
            dto.setProject(taskSpecAccessor.getProject());

            if (!StringUtils.hasText(taskSpecAccessor.getVersion())) {
                throw new IllegalArgumentException("spec: missing version");
            }
            String functionId = taskSpecAccessor.getVersion();

            //check if function exists
            Function fn = functionEntityService.find(functionId);
            if (fn == null) {
                throw new IllegalArgumentException("invalid function");
            }

            //check if a task for this kind already exists
            Optional<Task> existingTask = getTasksByFunctionId(functionId)
                .stream()
                .filter(t -> t.getKind().equals(dto.getKind()))
                .findFirst();
            if (existingTask.isPresent()) {
                throw new DuplicatedEntityException(EntityName.TASK.toString(), dto.getKind());
            }

            //create as new
            return entityService.create(dto);
        } catch (DuplicatedEntityException e) {
            throw new DuplicatedEntityException(EntityName.TASK.toString(), dto.getId());
        }
    }

    @Override
    public Task updateTask(@NotNull String id, @NotNull Task dto) throws NoSuchEntityException {
        log.debug("update task with id {}", String.valueOf(id));
        try {
            //fetch current and merge
            Task current = entityService.get(id);

            //hardcoded: function ref is not modifiable
            Map<String, Serializable> specMap = new HashMap<>();
            if (dto.getSpec() != null) {
                specMap.putAll(dto.getSpec());
            }
            if (current.getSpec() != null) {
                specMap.put("function", current.getSpec().get("function"));
            }

            TaskBaseSpec taskSpec = new TaskBaseSpec();
            taskSpec.configure(dto.getSpec());

            Spec spec = specRegistry.createSpec(dto.getKind(), EntityName.TASK, dto.getSpec());
            if (spec == null) {
                throw new IllegalArgumentException("invalid kind");
            }

            //TODO validate spec via validator
            //update spec as exported
            dto.setSpec(spec.toMap());

            //full update, task is modifiable
            return entityService.update(id, dto);
        } catch (NoSuchEntityException e) {
            throw new NoSuchEntityException(EntityName.TASK.toString());
        }
    }

    @Override
    public void deleteTask(@NotNull String id, @Nullable Boolean cascade) {
        log.debug("delete task with id {}", String.valueOf(id));

        Task task = findTask(id);
        if (task != null) {
            if (Boolean.TRUE.equals(cascade)) {
                log.debug("cascade delete runs for task with id {}", String.valueOf(id));

                //delete via async event to let manager do cleanups
                runService
                    .getRunsByTaskId(id)
                    .forEach(run -> {
                        log.debug("publish op: delete for {}", run.getId());
                        EntityOperation<Run> event = new EntityOperation<>(run, EntityAction.DELETE);
                        if (log.isTraceEnabled()) {
                            log.trace("event: {}", String.valueOf(event));
                        }

                        eventPublisher.publishEvent(event);
                    });
            }

            //delete the task
            entityService.delete(id);
        }
    }

    @Override
    public void deleteTasksByFunctionId(@NotNull String functionId) {
        log.debug("delete tasks for function {}", functionId);

        getTasksByFunctionId(functionId).forEach(task -> deleteTask(task.getId(), Boolean.TRUE));
    }

    private Specification<TaskEntity> createFunctionSpecification(String function) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("function"), function);
        };
    }
}
