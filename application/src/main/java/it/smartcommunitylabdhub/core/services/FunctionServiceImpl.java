package it.smartcommunitylabdhub.core.services;

import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.models.entities.function.Function;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.entities.task.Task;
import it.smartcommunitylabdhub.commons.models.entities.task.TaskBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.models.utils.RunUtils;
import it.smartcommunitylabdhub.commons.models.utils.TaskUtils;
import it.smartcommunitylabdhub.commons.services.EntityService;
import it.smartcommunitylabdhub.commons.services.FunctionService;
import it.smartcommunitylabdhub.core.models.entities.function.FunctionEntity;
import it.smartcommunitylabdhub.core.models.entities.run.RunEntity;
import it.smartcommunitylabdhub.core.models.entities.task.TaskEntity;
import it.smartcommunitylabdhub.core.models.queries.filters.entities.FunctionEntityFilter;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@Transactional
@Slf4j
public class FunctionServiceImpl implements FunctionService {

    @Autowired
    private EntityService<Function, FunctionEntity> entityService;

    @Autowired
    private EntityService<Task, TaskEntity> taskEntityService;

    @Autowired
    private EntityService<Run, RunEntity> runEntityService;

    @Override
    public Page<Function> getFunctions(Map<String, String> filter, Pageable pageable) {
        log.debug("list functions with {} page {}", String.valueOf(filter), pageable);

        FunctionEntityFilter functionEntityFilter = new FunctionEntityFilter();
        functionEntityFilter.setCreatedDate(filter.get("created"));
        functionEntityFilter.setName(filter.get("name"));
        functionEntityFilter.setKind(filter.get("kind"));
        Optional<State> stateOptional = Stream
            .of(State.values())
            .filter(state -> state.name().equals(filter.get("state")))
            .findAny();
        functionEntityFilter.setState(stateOptional.map(Enum::name).orElse(null));

        Specification<FunctionEntity> specification = createSpecification(filter, functionEntityFilter);
        return entityService.search(specification, pageable);
    }

    @Override
    public Function createFunction(Function functionDTO) throws DuplicatedEntityException {
        log.debug("create function");

        try {
            return entityService.create(functionDTO);
        } catch (DuplicatedEntityException e) {
            throw new DuplicatedEntityException(EntityName.FUNCTION.toString(), functionDTO.getId());
        }
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
    public void deleteFunction(@NotNull String id, Boolean cascade) {
        log.debug("delete function with id {}, cascade {}", String.valueOf(id), String.valueOf(cascade));

        Function function = entityService.find(id);
        if (function != null) {
            if (Boolean.TRUE.equals(cascade)) {
                // Remove Task
                List<Task> taskList = taskEntityService.searchAll(
                    createTaskSpecification(TaskUtils.buildTaskString(function))
                );

                //Delete all related sobject
                taskList.forEach(task -> {
                    //read base spec
                    TaskBaseSpec taskSpec = new TaskBaseSpec();
                    taskSpec.configure(task.getSpec());

                    // find and remove related runs
                    runEntityService
                        .searchAll(createRunSpecification(RunUtils.buildRunString(function, task)))
                        .forEach(r -> runEntityService.delete(r.getId()));

                    // remove task
                    taskEntityService.delete(task.getId());
                });
            }

            //remove function
            entityService.delete(id);
        }
    }

    @Deprecated
    @Override
    public List<Run> getFunctionRuns(String id) throws NoSuchEntityException {
        log.debug("get runs for function with id {}", String.valueOf(id));

        Function function = entityService.get(id);

        // Find and collect runs for a function
        return taskEntityService
            .searchAll(createTaskSpecification(TaskUtils.buildTaskString(function)))
            .stream()
            .flatMap(task -> {
                //read base spec
                TaskBaseSpec taskSpec = new TaskBaseSpec();
                taskSpec.configure(task.getSpec());

                // find  related runs
                return runEntityService
                    .searchAll(createRunSpecification(RunUtils.buildRunString(function, task)))
                    .stream();
            })
            .collect(Collectors.toList());
    }

    // @Override
    // public List<Function> getAllLatestFunctions() {
    //     try {
    //         List<FunctionEntity> functionList = this.functionRepository.findAllLatestFunctions();
    //         return functionList
    //             .stream()
    //             .map(function -> functionDTOBuilder.build(function, false))
    //             .collect(Collectors.toList());
    //     } catch (CustomException e) {
    //         throw new CoreException(
    //             ErrorList.FUNCTION_NOT_FOUND.getValue(),
    //             e.getMessage(),
    //             HttpStatus.INTERNAL_SERVER_ERROR
    //         );
    //     }
    // }

    protected Specification<FunctionEntity> createSpecification(
        Map<String, String> filter,
        FunctionEntityFilter entityFilter
    ) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            // Add your custom filter based on the provided map
            predicate = entityFilter.toPredicate(root, query, criteriaBuilder);

            // Add more conditions for other filter if needed

            return predicate;
        };
    }

    protected Specification<TaskEntity> createTaskSpecification(String function) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("function"), function);
        };
    }

    protected Specification<RunEntity> createRunSpecification(String task) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("task"), task);
        };
    }
}
