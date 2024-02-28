package it.smartcommunitylabdhub.core.services;

import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.models.entities.function.Function;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.queries.SearchFilter;
import it.smartcommunitylabdhub.commons.services.entities.TaskService;
import it.smartcommunitylabdhub.core.models.entities.function.FunctionEntity;
import it.smartcommunitylabdhub.core.models.entities.function.FunctionEntity_;
import it.smartcommunitylabdhub.core.models.entities.task.TaskEntity;
import it.smartcommunitylabdhub.core.models.queries.services.SearchableFunctionService;
import it.smartcommunitylabdhub.core.models.queries.specifications.CommonSpecification;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
@Transactional
@Slf4j
public class FunctionServiceImpl implements SearchableFunctionService {

    @Autowired
    private EntityService<Function, FunctionEntity> entityService;

    @Autowired
    private TaskService taskService;

    @Override
    public Page<Function> listFunctions(Pageable pageable) {
        log.debug("list functions page {}", pageable);

        return entityService.list(pageable);
    }

    @Override
    public Page<Function> searchFunctions(Pageable pageable, @Nullable SearchFilter<FunctionEntity> filter) {
        log.debug("list functions page {}, filter {}", pageable, String.valueOf(filter));

        Specification<FunctionEntity> specification = filter != null ? filter.toSpecification() : null;
        if (specification != null) {
            return entityService.search(specification, pageable);
        } else {
            return entityService.list(pageable);
        }
    }

    @Override
    public Page<Function> listLatestFunctionsByProject(@NotNull String project, Pageable pageable) {
        log.debug("list functions for project {} page {}", project, pageable);
        Specification<FunctionEntity> specification = Specification.allOf(
            CommonSpecification.projectEquals(project),
            CommonSpecification.latestByProject(project)
        );

        return entityService.search(specification, pageable);
    }

    @Override
    public Page<Function> searchLatestFunctionsByProject(
        @NotNull String project,
        Pageable pageable,
        @Nullable SearchFilter<FunctionEntity> filter
    ) {
        log.debug("list functions for project {} with {} page {}", project, String.valueOf(filter), pageable);
        Specification<FunctionEntity> filterSpecification = filter != null ? filter.toSpecification() : null;
        Specification<FunctionEntity> specification = Specification.allOf(
            CommonSpecification.projectEquals(project),
            CommonSpecification.latestByProject(project),
            filterSpecification
        );

        return entityService.search(specification, pageable);
    }

    @Override
    public List<Function> findFunctions(@NotNull String project, @NotNull String name) {
        log.debug("find functions for project {} with name {}", project, name);

        //fetch all versions ordered by date DESC
        Specification<FunctionEntity> where = Specification.allOf(
            CommonSpecification.projectEquals(project),
            CommonSpecification.nameEquals(name)
        );
        Specification<FunctionEntity> specification = (root, query, builder) -> {
            query.orderBy(builder.desc(root.get(FunctionEntity_.CREATED)));
            return where.toPredicate(root, query, builder);
        };

        return entityService.searchAll(specification);
    }

    @Override
    public Page<Function> findFunctions(@NotNull String project, @NotNull String name, Pageable pageable) {
        log.debug("find functions for project {} with name {} page {}", project, name, pageable);

        //fetch all versions ordered by date DESC
        Specification<FunctionEntity> where = Specification.allOf(
            CommonSpecification.projectEquals(project),
            CommonSpecification.nameEquals(name)
        );
        Specification<FunctionEntity> specification = (root, query, builder) -> {
            query.orderBy(builder.desc(root.get(FunctionEntity_.CREATED)));
            return where.toPredicate(root, query, builder);
        };

        return entityService.search(specification, pageable);
    }

    @Override
    public Function findFunction(@NotNull String id) {
        log.debug("find function with id {}", String.valueOf(id));

        return entityService.find(id);
    }

    @Override
    public Function getFunction(@NotNull String id) throws NoSuchEntityException {
        log.debug("get function with id {}", String.valueOf(id));

        try {
            return entityService.get(id);
        } catch (NoSuchEntityException e) {
            throw new NoSuchEntityException(EntityName.FUNCTION.toString());
        }
    }

    @Override
    public Function getLatestFunction(@NotNull String project, @NotNull String name) throws NoSuchEntityException {
        log.debug("get latest function for project {} with name {}", project, name);

        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getLatestFunction'");
    }

    @Override
    public Function createFunction(@NotNull Function functionDTO) throws DuplicatedEntityException {
        log.debug("create function");

        try {
            return entityService.create(functionDTO);
        } catch (DuplicatedEntityException e) {
            throw new DuplicatedEntityException(EntityName.FUNCTION.toString(), functionDTO.getId());
        }
    }

    @Override
    public Function updateFunction(@NotNull String id, @NotNull Function functionDTO) throws NoSuchEntityException {
        log.debug("update function with id {}", String.valueOf(id));
        try {
            //fetch current and merge
            Function current = entityService.get(id);

            //spec is not modificable: enforce current
            functionDTO.setSpec(current.getSpec());

            //update
            return entityService.update(id, functionDTO);
        } catch (NoSuchEntityException e) {
            throw new NoSuchEntityException(EntityName.FUNCTION.toString());
        }
    }

    @Override
    public void deleteFunction(@NotNull String id, @Nullable Boolean cascade) {
        log.debug("delete function with id {}", String.valueOf(id));

        Function function = findFunction(id);
        if (function != null) {
            if (Boolean.TRUE.equals(cascade)) {
                //tasks
                log.debug("cascade delete tasks for function with id {}", String.valueOf(id));
                taskService.deleteTasksByFunctionId(id);
            }

            //delete the function
            entityService.delete(id);
        }
    }

    @Override
    public void deleteFunctions(@NotNull String project, @NotNull String name) {
        log.debug("delete functions for project {} with name {}", project, name);

        //delete with cascade
        findFunctions(project, name).forEach(function -> deleteFunction(function.getId(), Boolean.TRUE));
    }

    // @Override
    // public List<Task> listTasksByFunction(@NotNull String id) throws NoSuchEntityException {
    //     log.debug("list tasks for function {}", id);

    //     Function function = getFunction(id);

    //     //define a spec for tasks building function path
    //     Specification<TaskEntity> where = Specification.allOf(
    //         CommonSpecification.projectEquals(function.getProject()),
    //         createTaskSpecification(TaskUtils.buildFunctionString(function))
    //     );

    //     //fetch all tasks ordered by kind ASC
    //     Specification<TaskEntity> specification = (root, query, builder) -> {
    //         query.orderBy(builder.asc(root.get(TaskEntity_.KIND)));

    //         return where.toPredicate(root, query, builder);
    //     };

    //     return taskEntityService.searchAll(specification);
    // }

    // @Override
    // public void deleteTasksByFunction(@NotNull String id) throws NoSuchEntityException {
    //     log.debug("delete tasks for function {}", id);

    //     Function function = getFunction(id);

    //     //define a spec for tasks building function path
    //     Specification<TaskEntity> where = Specification.allOf(
    //         CommonSpecification.projectEquals(function.getProject()),
    //         createTaskSpecification(TaskUtils.buildFunctionString(function))
    //     );

    //     //fetch all tasks ordered by kind ASC
    //     Specification<TaskEntity> specification = (root, query, builder) -> {
    //         query.orderBy(builder.asc(root.get(TaskEntity_.KIND)));

    //         return where.toPredicate(root, query, builder);
    //     };

    //     long count = taskEntityService.deleteAll(specification);
    //     log.debug("deleted tasks count {}", count);
    // }

    private Specification<TaskEntity> createTaskSpecification(String function) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("function"), function);
        };
    }
}
