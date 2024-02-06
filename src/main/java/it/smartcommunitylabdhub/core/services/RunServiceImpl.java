package it.smartcommunitylabdhub.core.services;

import it.smartcommunitylabdhub.core.components.fsm.enums.RunState;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.runnables.Runnable;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.runtimes.Runtime;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.runtimes.RuntimeFactory;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecRegistry;
import it.smartcommunitylabdhub.core.exceptions.CoreException;
import it.smartcommunitylabdhub.core.exceptions.CustomException;
import it.smartcommunitylabdhub.core.models.accessors.utils.TaskAccessor;
import it.smartcommunitylabdhub.core.models.accessors.utils.TaskUtils;
import it.smartcommunitylabdhub.core.models.base.interfaces.Spec;
import it.smartcommunitylabdhub.core.models.builders.run.RunDTOBuilder;
import it.smartcommunitylabdhub.core.models.builders.run.RunEntityBuilder;
import it.smartcommunitylabdhub.core.models.entities.function.specs.FunctionBaseSpec;
import it.smartcommunitylabdhub.core.models.entities.run.Run;
import it.smartcommunitylabdhub.core.models.entities.run.RunEntity;
import it.smartcommunitylabdhub.core.models.entities.run.specs.RunBaseSpec;
import it.smartcommunitylabdhub.core.models.entities.task.specs.TaskBaseSpec;
import it.smartcommunitylabdhub.core.models.queries.filters.abstracts.AbstractSpecificationService;
import it.smartcommunitylabdhub.core.models.queries.filters.entities.RunEntityFilter;
import it.smartcommunitylabdhub.core.repositories.RunRepository;
import it.smartcommunitylabdhub.core.services.interfaces.FunctionService;
import it.smartcommunitylabdhub.core.services.interfaces.RunService;
import it.smartcommunitylabdhub.core.services.interfaces.RunnableStoreService;
import it.smartcommunitylabdhub.core.services.interfaces.TaskService;
import it.smartcommunitylabdhub.core.utils.ErrorList;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class RunServiceImpl extends AbstractSpecificationService<RunEntity, RunEntityFilter>
        implements RunService {

    @Autowired
    RunDTOBuilder runDTOBuilder;

    @Autowired
    RunRepository runRepository;

    @Autowired
    TaskService taskService;

    @Autowired
    FunctionService functionService;

    @Autowired
    RuntimeFactory runtimeFactory;

    @Autowired
    RunEntityFilter runEntityFilter;

    @Autowired
    RunEntityBuilder runEntityBuilder;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    SpecRegistry<? extends Spec> specRegistry;

    @Autowired
    RunnableStoreService<Runnable> runnableStoreService;

    @Override
    public Page<Run> getRuns(Map<String, String> filter, Pageable pageable) {
        try {
            runEntityFilter.setTask(filter.get("task"));
            runEntityFilter.setTaskId(filter.get("task_id"));
            runEntityFilter.setKind(filter.get("kind"));
            runEntityFilter.setCreatedDate(filter.get("created"));
            Optional<RunState> stateOptional = Stream.of(RunState.values())
                    .filter(state -> state.name().equals(filter.get("state")))
                    .findAny();
            runEntityFilter.setState(stateOptional.map(Enum::name).orElse(null));

            Specification<RunEntity> specification = createSpecification(filter, runEntityFilter);

            Page<RunEntity> runPage = this.runRepository.findAll(specification, pageable);

            return new PageImpl<>(
                    runPage.getContent().stream().map(run -> runDTOBuilder.build(run))
                            .collect(Collectors.toList()),
                    pageable,
                    runPage.getTotalElements());

        } catch (CustomException e) {
            throw new CoreException(ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Run getRun(String uuid) {
        return runRepository.findById(uuid).map(run -> runDTOBuilder.build(run))
                .orElseThrow(() -> new CoreException(
                        ErrorList.RUN_NOT_FOUND.getValue(),
                        ErrorList.RUN_NOT_FOUND.getReason(),
                        HttpStatus.NOT_FOUND));
    }

    @Override
    public boolean deleteRun(String uuid, Boolean cascade) {
        try {
            this.runRepository.deleteById(uuid);
            return true;
        } catch (Exception e) {
            throw new CoreException(ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                    "cannot delete artifact",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public boolean deleteRunByTaskId(String uuid) {
        try {
            this.runRepository.deleteByTaskId(uuid);
            return true;
        } catch (Exception e) {
            throw new CoreException(ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                    "cannot delete artifact",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Run save(Run runDTO) {

        return Optional.of(this.runRepository.saveAndFlush(runEntityBuilder.build(runDTO)))
                .map(run -> runDTOBuilder.build(run))
                .orElseThrow(() -> new CoreException(
                        "RunSaveError",
                        "Problem while saving the run.",
                        HttpStatus.NOT_FOUND));
    }

    @Override
    public Run updateRun(Run runDTO, String uuid) {

        if (!runDTO.getId().equals(uuid)) {
            throw new CoreException(
                    ErrorList.RUN_NOT_MATCH.getValue(),
                    ErrorList.RUN_NOT_MATCH.getReason(),
                    HttpStatus.NOT_FOUND);
        }

        final RunEntity run = runRepository.findById(uuid).orElse(null);
        if (run == null) {
            throw new CoreException(
                    ErrorList.RUN_NOT_FOUND.getValue(),
                    ErrorList.RUN_NOT_FOUND.getReason(),
                    HttpStatus.NOT_FOUND);
        }

        try {
            final RunEntity runUpdated = runEntityBuilder.update(run, runDTO);
            this.runRepository.saveAndFlush(runUpdated);
            return runDTOBuilder.build(runUpdated);
        } catch (CustomException e) {
            throw new CoreException(
                    ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public <F extends FunctionBaseSpec> Run createRun(Run runDTO) {

        // Retrieve Run base spec
        RunBaseSpec runBaseSpec = specRegistry.createSpec(
                runDTO.getKind(),
                EntityName.RUN,
                runDTO.getSpec()
        );


        // Check if run already exist with the passed uuid
        if (runRepository.existsById(Optional.ofNullable(runDTO.getId()).orElse(""))) {
            throw new CoreException(
                    ErrorList.DUPLICATE_RUN.getValue(),
                    ErrorList.DUPLICATE_RUN.getReason(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Retrieve task
        return Optional.ofNullable(this.taskService.getTask(runBaseSpec.getTaskId()))
                .map(taskDTO -> {
                    TaskBaseSpec taskBaseSpec = specRegistry.createSpec(
                            taskDTO.getKind(),
                            EntityName.TASK,
                            taskDTO.getSpec());

                    // Parse task to get accessor
                    TaskAccessor taskAccessor = TaskUtils.parseTask(taskBaseSpec.getFunction());

                    return Optional
                            .ofNullable(functionService.getFunction(
                                    taskAccessor.getVersion()))
                            .map(functionDTO -> {

                                // Update spec object for run
                                runDTO.setProject(taskAccessor.getProject());

                                // Check weather the run has local set to True in that case return
                                // immediately the run without invoke the execution.
                                Supplier<Run> result = () -> Optional
                                        .of(runBaseSpec.getLocalExecution()) // if true save and return
                                        .filter(value -> value.equals(true))

                                        .map(value -> {
                                            // Save the run and return immediately
                                            RunEntity run = runRepository.saveAndFlush(
                                                    runEntityBuilder.build(runDTO));
                                            return runDTOBuilder.build(run);
                                        })
                                        // exec run and return run dto
                                        .orElseGet(() -> {

                                            // Retrieve Runtime and build run
                                            Runtime<? extends FunctionBaseSpec, ? extends RunBaseSpec, ? extends Runnable> runtime =
                                                    runtimeFactory.getRuntime(taskAccessor.getRuntime());


                                            // Build RunSpec using Runtime now if wrong type is passed to a specific runtime
                                            // an exception occur! for.
                                            RunBaseSpec runSpecBuilt = runtime.build(
                                                    specRegistry.createSpec(
                                                            functionDTO.getKind(),
                                                            EntityName.FUNCTION,
                                                            functionDTO.getSpec()),
                                                    taskBaseSpec,
                                                    runBaseSpec,
                                                    taskDTO.getKind()
                                            );

                                            // Update run spec
                                            runDTO.setSpec(runSpecBuilt.toMap());

                                            // Update run state to BUILT
                                            runDTO.getStatus().put("state", RunState.BUILT.toString());

                                            // Save Run
                                            RunEntity run = runRepository.saveAndFlush(
                                                    runEntityBuilder.build(runDTO)
                                            );

                                            // Create Runnable
                                            Runnable runnable = runtime.run(
                                                    runDTOBuilder.build(run)
                                            );

                                            // Store runnable
                                            runnableStoreService.store(
                                                    runnable.getId(),
                                                    runnable
                                            );

                                            // Dispatch Runnable
                                            eventPublisher.publishEvent(runnable);

                                            // Return saved run
                                            return runDTOBuilder.build(run);
                                        });

                                return result.get();
                            }).orElseThrow(() -> new CoreException(
                                    ErrorList.FUNCTION_NOT_FOUND.getValue(),
                                    ErrorList.FUNCTION_NOT_FOUND.getReason(),
                                    HttpStatus.NOT_FOUND));


                })
                .orElseThrow(() -> new CoreException(
                        ErrorList.RUN_NOT_FOUND.getValue(),
                        ErrorList.RUN_NOT_FOUND.getReason(),
                        HttpStatus.NOT_FOUND));

    }
}
