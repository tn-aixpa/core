package it.smartcommunitylabdhub.core.services;

import it.smartcommunitylabdhub.commons.accessors.spec.RunSpecAccessor;
import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.models.entities.function.Function;
import it.smartcommunitylabdhub.commons.models.entities.function.FunctionBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseStatus;
import it.smartcommunitylabdhub.commons.models.entities.task.Task;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.queries.SearchFilter;
import it.smartcommunitylabdhub.commons.models.specs.Spec;
import it.smartcommunitylabdhub.commons.models.utils.RunUtils;
import it.smartcommunitylabdhub.commons.models.utils.TaskUtils;
import it.smartcommunitylabdhub.commons.services.SpecRegistry;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.runtimes.RuntimeFactory;
import it.smartcommunitylabdhub.core.models.entities.function.FunctionEntity;
import it.smartcommunitylabdhub.core.models.entities.run.RunEntity;
import it.smartcommunitylabdhub.core.models.entities.run.RunEntity_;
import it.smartcommunitylabdhub.core.models.entities.task.TaskEntity;
import it.smartcommunitylabdhub.core.models.queries.services.SearchableRunService;
import it.smartcommunitylabdhub.core.models.queries.specifications.CommonSpecification;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
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
public class RunServiceImpl implements SearchableRunService {

    @Autowired
    SpecRegistry specRegistry;
    @Autowired
    private EntityService<Run, RunEntity> entityService;
    @Autowired
    private EntityService<Task, TaskEntity> taskEntityService;
    @Autowired
    private EntityService<Function, FunctionEntity> functionEntityService;
    //TODO move to RunManager
    @Autowired
    private RuntimeFactory runtimeFactory;

    //TODO move to RunManager
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public Page<Run> listRuns(Pageable pageable) {
        log.debug("list runs page {}", pageable);

        return entityService.list(pageable);
    }

    @Override
    public Page<Run> searchRuns(Pageable pageable, @Nullable SearchFilter<RunEntity> filter) {
        log.debug("list runs page {}, filter {}", pageable, String.valueOf(filter));

        Specification<RunEntity> specification = filter != null ? filter.toSpecification() : null;
        if (specification != null) {
            return entityService.search(specification, pageable);
        } else {
            return entityService.list(pageable);
        }
    }

    @Override
    public Page<Run> listRunsByProject(@NotNull String project, Pageable pageable) {
        log.debug("list runs for project {} page {}", project, pageable);
        Specification<RunEntity> specification = Specification.allOf(CommonSpecification.projectEquals(project));

        return entityService.search(specification, pageable);
    }

    @Override
    public Page<Run> searchRunsByProject(
            @NotNull String project,
            Pageable pageable,
            @Nullable SearchFilter<RunEntity> filter
    ) {
        log.debug("list runs for project {} with {} page {}", project, String.valueOf(filter), pageable);
        Specification<RunEntity> filterSpecification = filter != null ? filter.toSpecification() : null;
        Specification<RunEntity> specification = Specification.allOf(
                CommonSpecification.projectEquals(project),
                filterSpecification
        );

        return entityService.search(specification, pageable);
    }

    @Override
    public List<Run> getRunsByTaskId(@NotNull String taskId) {
        log.debug("list runs for task {}", taskId);

        Task task = taskEntityService.find(taskId);
        if (task == null) {
            return Collections.emptyList();
        }

        //define a spec for runs building task path
        Specification<RunEntity> where = Specification.allOf(
                CommonSpecification.projectEquals(task.getProject()),
                createTaskSpecification(RunUtils.buildTaskString(task))
        );

        //fetch all runs ordered by created DESC
        Specification<RunEntity> specification = (root, query, builder) -> {
            query.orderBy(builder.desc(root.get(RunEntity_.CREATED)));
            return where.toPredicate(root, query, builder);
        };

        return entityService.searchAll(specification);
    }

    @Override
    public Run findRun(@NotNull String id) {
        log.debug("find run with id {}", String.valueOf(id));

        return entityService.find(id);
    }

    @Override
    public Run getRun(@NotNull String id) throws NoSuchEntityException {
        log.debug("get run with id {}", String.valueOf(id));

        try {
            return entityService.get(id);
        } catch (NoSuchEntityException e) {
            throw new NoSuchEntityException(EntityName.RUN.toString());
        }
    }

    @Override
    public Run createRun(@NotNull Run dto) throws DuplicatedEntityException {
        log.debug("create run");
        if (log.isTraceEnabled()) {
            log.trace("dto: {}", dto);
        }

        //validate project
        String project = dto.getProject();
        if (!StringUtils.hasText(project)) {
            throw new IllegalArgumentException("invalid or missing project");
        }

        //TODO check if project exists?

        //check base run spec
        RunBaseSpec runSpec = new RunBaseSpec();
        runSpec.configure(dto.getSpec());

        // Parse and export Spec
        Spec spec = specRegistry.createSpec(dto.getKind(), EntityName.RUN, dto.getSpec());
        if (spec == null) {
            throw new IllegalArgumentException("invalid kind");
        }

        //TODO validate spec via validator
        //update spec as exported
        dto.setSpec(spec.toMap());

        String taskPath = runSpec.getTask();
        if (!StringUtils.hasText(taskPath)) {
            throw new IllegalArgumentException("missing task");
        }

        RunSpecAccessor runSpecAccessor = RunUtils.parseTask(taskPath);
        if (!StringUtils.hasText(runSpecAccessor.getProject())) {
            throw new IllegalArgumentException("spec: missing project");
        }

        //check project match
        if (!project.equals(runSpecAccessor.getProject())) {
            throw new IllegalArgumentException("project mismatch");
        }
        if (!StringUtils.hasText(runSpecAccessor.getTask())) {
            throw new IllegalArgumentException("spec: missing task");
        }
        if (!StringUtils.hasText(runSpecAccessor.getFunction())) {
            throw new IllegalArgumentException("spec: missing function");
        }
        if (!StringUtils.hasText(runSpecAccessor.getVersion())) {
            throw new IllegalArgumentException("spec: missing version");
        }
        String functionId = runSpecAccessor.getVersion();

        //check if function exists and matches
        Function function = functionEntityService.find(functionId);
        if (function == null) {
            throw new IllegalArgumentException("invalid function");
        }
        if (!project.equals(function.getProject())) {
            throw new IllegalArgumentException("project mismatch");
        }
        if (!function.getName().equals(runSpecAccessor.getFunction())) {
            throw new IllegalArgumentException("function name mismatch");
        }

        // retrieve task by looking up value
        // define a spec for matching task
        Specification<TaskEntity> where = Specification.allOf(
                CommonSpecification.projectEquals(function.getProject()),
                createFunctionSpecification(TaskUtils.buildFunctionString(function)),
                createTaskKindSpecification(runSpecAccessor.getTask())
        );

        Task task = taskEntityService.searchAll(where).stream().findFirst().orElse(null);
        if (task == null) {
            throw new IllegalArgumentException("invalid task");
        }

        try {
            //TODO move build+exec to dedicated methods and handle state changes!
            if (Optional.ofNullable(runSpec.getLocalExecution()).orElse(Boolean.FALSE).booleanValue() == false) {
                // Retrieve Runtime and build run
                Runtime<? extends FunctionBaseSpec, ? extends RunBaseSpec, ? extends RunBaseStatus, ? extends Runnable> runtime =
                        runtimeFactory.getRuntime(function.getKind());

                // Build RunSpec using Runtime now if wrong type is passed to a specific runtime
                // an exception occur! for.
                RunBaseSpec runSpecBuilt = runtime.build(function, task, dto);

                // Update run spec
                dto.setSpec(runSpecBuilt.toMap());

                // Update run state to BUILT
                dto.getStatus().put("state", State.BUILT.toString());

                if (log.isTraceEnabled()) {
                    log.trace("built run: {}", dto);
                }
            }


            //
            //TODO move build+exec to dedicated methods and handle state changes!
            if (Optional.ofNullable(runSpec.getLocalExecution()).orElse(Boolean.FALSE).booleanValue() == false) {
                // Retrieve Runtime and build run
                Runtime<? extends FunctionBaseSpec, ? extends RunBaseSpec, ? extends RunBaseStatus, ? extends Runnable> runtime =
                        runtimeFactory.getRuntime(function.getKind());

                // Create Runnable
                Runnable runnable = runtime.run(run);

                // Dispatch Runnable
                eventPublisher.publishEvent(runnable);
            }

            return entityService.create(dto);
        } catch (DuplicatedEntityException e) {
            throw new DuplicatedEntityException(EntityName.RUN.toString(), dto.getId());
        }
    }

    @Override
    public Run updateRun(@NotNull String id, @NotNull Run runDTO) throws NoSuchEntityException {
        log.debug("update run with id {}", String.valueOf(id));
        try {
            //fetch current and merge
            Run current = entityService.get(id);

            //spec is not modifiable
            runDTO.setSpec(current.getSpec());

            //full update, run is modifiable
            return entityService.update(id, runDTO);
        } catch (NoSuchEntityException e) {
            throw new NoSuchEntityException(EntityName.RUN.toString());
        }
    }

    @Override
    public void deleteRun(@NotNull String id, @Nullable Boolean cascade) {
        log.debug("delete run with id {}", String.valueOf(id));

        Run run = findRun(id);
        if (run != null) {
            if (Boolean.TRUE.equals(cascade)) {
                log.debug("cascade delete logs for run with id {}", String.valueOf(id));
                //TODO
            }

            //delete the run
            entityService.delete(id);
        }

        entityService.delete(id);
    }

    @Override
    public void deleteRunsByTaskId(@NotNull String taskId) {
        log.debug("delete runs for task {}", taskId);

        getRunsByTaskId(taskId).forEach(run -> deleteRun(run.getId(), Boolean.TRUE));
    }

    @Override
    public Run buildRun(@NotNull @Valid Run dto) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'buildRun'");
    }

    @Override
    public Run execRun(@NotNull @Valid Run dto) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'execRun'");
    }

    private Specification<RunEntity> createTaskSpecification(String task) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("task"), task);
        };
    }

    private Specification<TaskEntity> createFunctionSpecification(String function) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("function"), function);
        };
    }

    private Specification<TaskEntity> createTaskKindSpecification(String kind) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("kind"), kind);
        };
    }
}
