package it.smartcommunitylabdhub.core.services;

import it.smartcommunitylabdhub.commons.accessors.spec.RunSpecAccessor;
import it.smartcommunitylabdhub.commons.accessors.spec.TaskSpecAccessor;
import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.infrastructure.Runnable;
import it.smartcommunitylabdhub.commons.infrastructure.Runtime;
import it.smartcommunitylabdhub.commons.models.entities.function.Function;
import it.smartcommunitylabdhub.commons.models.entities.function.FunctionBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.run.RunState;
import it.smartcommunitylabdhub.commons.models.entities.task.Task;
import it.smartcommunitylabdhub.commons.models.entities.task.TaskBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.utils.RunUtils;
import it.smartcommunitylabdhub.commons.models.utils.TaskUtils;
import it.smartcommunitylabdhub.commons.services.FunctionService;
import it.smartcommunitylabdhub.commons.services.SpecRegistry;
import it.smartcommunitylabdhub.commons.services.entities.RunService;
import it.smartcommunitylabdhub.commons.services.entities.TaskService;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.runtimes.RuntimeFactory;
import it.smartcommunitylabdhub.core.models.builders.run.RunDTOBuilder;
import it.smartcommunitylabdhub.core.models.builders.run.RunEntityBuilder;
import it.smartcommunitylabdhub.core.models.entities.run.RunEntity;
import it.smartcommunitylabdhub.core.models.queries.filters.abstracts.AbstractSpecificationService;
import it.smartcommunitylabdhub.core.models.queries.filters.entities.RunEntityFilter;
import it.smartcommunitylabdhub.core.repositories.RunRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Transactional
@Slf4j
public class RunServiceImpl extends AbstractSpecificationService<RunEntity, RunEntityFilter> implements RunService {

    @Autowired
    private RunRepository runRepository;

    @Autowired
    private RunDTOBuilder runDTOBuilder;

    @Autowired
    private RunEntityBuilder runEntityBuilder;

    @Autowired
    private TaskService taskService;

    @Autowired
    private FunctionService functionService;

    @Autowired
    private RuntimeFactory runtimeFactory;

    @Autowired
    private SpecRegistry specRegistry;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public Page<Run> getRuns(Map<String, String> filter, Pageable pageable) {
        RunEntityFilter runEntityFilter = new RunEntityFilter();
        runEntityFilter.setTask(filter.get("task"));
        runEntityFilter.setTaskId(filter.get("task_id"));
        runEntityFilter.setKind(filter.get("kind"));
        runEntityFilter.setCreatedDate(filter.get("created"));
        Optional<RunState> stateOptional = Stream
            .of(RunState.values())
            .filter(state -> state.name().equals(filter.get("state")))
            .findAny();
        runEntityFilter.setState(stateOptional.map(Enum::name).orElse(null));

        Specification<RunEntity> specification = createSpecification(filter, runEntityFilter);

        Page<RunEntity> runPage = runRepository.findAll(specification, pageable);
        List<Run> content = runPage
            .getContent()
            .stream()
            .map(run -> runDTOBuilder.build(run))
            .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, runPage.getTotalElements());
    }

    @Override
    public List<Run> getRunsByTask(@NotNull String task) {
        log.debug("get runs for task {}", task);

        return runRepository.findByTask(task).stream().map(e -> runDTOBuilder.build(e)).collect(Collectors.toList());
    }

    @Override
    public Run findRun(@NotNull String id) {
        log.debug("find run with id {}", id);
        Run run = runRepository.findById(id).map(e -> runDTOBuilder.build(e)).orElse(null);
        if (log.isTraceEnabled()) {
            log.trace("run: {}", run);
        }

        return run;
    }

    @Override
    public Run getRun(@NotNull String id) throws NoSuchEntityException {
        log.debug("get run with id {}", id);

        return Optional.ofNullable(findRun(id)).orElseThrow(() -> new NoSuchEntityException(EntityName.RUN.name()));
    }

    @Override
    public void deleteRun(@NotNull String id, Boolean cascade) {
        log.debug("delete run with id {}", id);

        Optional.ofNullable(findRun(id)).ifPresent(r -> runRepository.deleteById(r.getId()));
    }

    @Override
    public void deleteRunsByTask(@NotNull String task) {
        log.debug("delete runs for task {}", task);

        runRepository.deleteByTask(task);
    }

    @Override
    public Run updateRun(String id, @Valid Run dto) throws NoSuchEntityException {
        log.debug("update run with id {}", id);

        //lookup via id
        RunEntity run = runRepository.findById(id).orElse(null);
        if (run == null) {
            log.debug("no run with id {}", id);
            throw new NoSuchEntityException("run");
        }

        if (log.isTraceEnabled()) {
            log.trace("run: {}", run);
        }

        if (log.isTraceEnabled()) {
            log.trace("dto: {}", dto);
        }

        //TODO validate DTO

        //convert DTO
        RunEntity entity = runEntityBuilder.build(dto);

        //update only allowed fields
        run.setMetadata(entity.getMetadata());
        run.setExtra(entity.getExtra());
        run.setStatus(entity.getStatus());

        //TODO handle state changes via state machine, refuse invalid changes
        //if state is CREATED we can update spec to allow prebuilt
        run.setState(entity.getState());

        //persist
        run = runRepository.saveAndFlush(run);

        if (log.isTraceEnabled()) {
            log.trace("run: {}", run);
        }

        return runDTOBuilder.build(run);
    }

    @Override
    public Run createRun(@NotNull Run dto) throws NoSuchEntityException, DuplicatedEntityException {
        log.debug("create run with id {}", String.valueOf(dto.getId()));
        if (log.isTraceEnabled()) {
            log.trace("dto: {}", dto);
        }

        //validate project
        String project = dto.getProject();
        if (!StringUtils.hasText(project)) {
            throw new IllegalArgumentException("invalid or missing project");
        }

        //TODO check if project exists

        // Retrieve Run base spec
        RunBaseSpec runSpec = specRegistry.createSpec(dto.getKind(), EntityName.RUN, dto.getSpec());

        if (dto.getId() != null && (runRepository.existsById(dto.getId()))) {
            throw new DuplicatedEntityException(EntityName.RUN.name(), dto.getId());
        }

        //TODO validate DTO

        // Create string run accessor from task
        RunSpecAccessor runAccessor = RunUtils.parseRun(runSpec.getTask());

        // retrieve function
        Function function = functionService.getFunction(runAccessor.getVersion());
        if (!function.getKind().equals(runAccessor.getFunction())) {
            throw new IllegalArgumentException("function runtime mismatch");
        }

        // retrieve task by looking up value
        Task task = taskService
            .getTasksByFunction(function.getId())
            .stream()
            .filter(t -> t.getKind().equals(runAccessor.getTask()))
            .findFirst()
            .orElse(null);

        if (task == null) {
            throw new NoSuchEntityException(EntityName.TASK.name());
        }

        //check project
        if (!project.equals(function.getProject()) || !project.equals(task.getProject())) {
            throw new IllegalArgumentException("project mismatch");
        }

        //TODO move build+exec to dedicated methods and handle state changes!
        if (Optional.ofNullable(runSpec.getLocalExecution()).orElse(Boolean.FALSE).booleanValue() == false) {
            //derive runtime from task spec
            TaskBaseSpec taskSpec = specRegistry.createSpec(task.getKind(), EntityName.TASK, task.getSpec());
            TaskSpecAccessor taskAccessor = TaskUtils.parseTask(taskSpec.getFunction());

            // Retrieve Runtime and build run
            Runtime<? extends FunctionBaseSpec, ? extends RunBaseSpec, ? extends Runnable> runtime =
                runtimeFactory.getRuntime(taskAccessor.getRuntime());

            // Build RunSpec using Runtime now if wrong type is passed to a specific runtime
            // an exception occur! for.
            RunBaseSpec runSpecBuilt = runtime.build(function, task, dto);

            // Update run spec
            dto.setSpec(runSpecBuilt.toMap());

            // Update run state to BUILT
            dto.getStatus().put("state", RunState.BUILT.toString());
        }

        //build entity
        RunEntity entity = runEntityBuilder.build(dto);
        entity.setTaskId(task.getId());

        if (log.isTraceEnabled()) {
            log.trace("run: {}", entity);
        }

        //persist
        entity = runRepository.saveAndFlush(entity);

        Run run = runDTOBuilder.build(entity);

        //TODO move build+exec to dedicated methods and handle state changes!
        if (Optional.ofNullable(runSpec.getLocalExecution()).orElse(Boolean.FALSE).booleanValue() == false) {
            //derive runtime from task spec
            TaskBaseSpec taskSpec = specRegistry.createSpec(task.getKind(), EntityName.TASK, task.getSpec());
            TaskSpecAccessor taskAccessor = TaskUtils.parseTask(taskSpec.getFunction());

            // Retrieve Runtime and build run
            Runtime<? extends FunctionBaseSpec, ? extends RunBaseSpec, ? extends Runnable> runtime =
                runtimeFactory.getRuntime(taskAccessor.getRuntime());

            // Create Runnable
            Runnable runnable = runtime.run(run);

            // Dispatch Runnable
            eventPublisher.publishEvent(runnable);
        }

        return run;
        // TaskBaseSpec taskBaseSpec = specRegistry.createSpec(taskDTO.getKind(), EntityName.TASK, taskDTO.getSpec());

        // // Parse task to get accessor
        // TaskSpecAccessor taskAccessor = TaskUtils.parseTask(taskBaseSpec.getFunction());

        // return Optional
        //     .ofNullable(functionService.getFunction(taskAccessor.getVersion()))
        //     .map(functionDTO -> {
        //         // Update spec object for run
        //         runDTO.setProject(taskAccessor.getProject());

        //         // Check weather the run has local set to True in that case return
        //         // immediately the run without invoke the execution.
        //         Supplier<Run> result = () ->
        //             Optional
        //                 .of(runBaseSpec.getLocalExecution()) // if true save and return
        //                 .filter(value -> value.equals(true))
        //                 .map(value -> {
        //                     // Save the run and return immediately
        //                     RunEntity run = runRepository.saveAndFlush(runEntityBuilder.build(runDTO));
        //                     return runDTOBuilder.build(run);
        //                 })
        //                 // exec run and return run dto
        //                 .orElseGet(() -> {
        //                     // Retrieve Runtime and build run
        //                     Runtime<? extends FunctionBaseSpec, ? extends RunBaseSpec, ? extends Runnable> runtime =
        //                         runtimeFactory.getRuntime(taskAccessor.getRuntime());

        //                     // Build RunSpec using Runtime now if wrong type is passed to a specific runtime
        //                     // an exception occur! for.
        //                     RunBaseSpec runSpecBuilt = runtime.build(functionDTO, taskDTO, runDTO);

        //                     // Update run spec
        //                     runDTO.setSpec(runSpecBuilt.toMap());

        //                     // Update run state to BUILT
        //                     runDTO.getStatus().put("state", RunState.BUILT.toString());

        //                     // Save Run
        //                     RunEntity run = runRepository.saveAndFlush(runEntityBuilder.build(runDTO));

        //                     // Create Runnable
        //                     Runnable runnable = runtime.run(runDTOBuilder.build(run));

        //                     // Dispatch Runnable
        //                     eventPublisher.publishEvent(runnable);

        //                     // Return saved run
        //                     return runDTOBuilder.build(run);
        //                 });

        //         return result.get();
        //     })
        //     .orElseThrow(() ->
        //         new CoreException(
        //             ErrorList.FUNCTION_NOT_FOUND.getValue(),
        //             ErrorList.FUNCTION_NOT_FOUND.getReason(),
        //             HttpStatus.NOT_FOUND
        //         )
        //     );
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
}
