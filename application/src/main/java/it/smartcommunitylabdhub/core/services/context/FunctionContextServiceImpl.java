package it.smartcommunitylabdhub.core.services.context;

import it.smartcommunitylabdhub.commons.exceptions.CoreException;
import it.smartcommunitylabdhub.commons.exceptions.CustomException;
import it.smartcommunitylabdhub.commons.models.accessors.utils.TaskUtils;
import it.smartcommunitylabdhub.commons.models.entities.function.Function;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.utils.ErrorList;
import it.smartcommunitylabdhub.core.models.builders.function.FunctionDTOBuilder;
import it.smartcommunitylabdhub.core.models.builders.function.FunctionEntityBuilder;
import it.smartcommunitylabdhub.core.models.entities.function.FunctionEntity;
import it.smartcommunitylabdhub.core.models.entities.task.TaskEntity;
import it.smartcommunitylabdhub.core.models.queries.filters.entities.FunctionEntityFilter;
import it.smartcommunitylabdhub.core.models.queries.specifications.CommonSpecification;
import it.smartcommunitylabdhub.core.repositories.FunctionRepository;
import it.smartcommunitylabdhub.core.repositories.RunRepository;
import it.smartcommunitylabdhub.core.repositories.TaskRepository;
import it.smartcommunitylabdhub.core.services.context.interfaces.FunctionContextService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class FunctionContextServiceImpl
    extends ContextService<FunctionEntity, FunctionEntityFilter>
    implements FunctionContextService {

    @Autowired
    FunctionRepository functionRepository;

    @Autowired
    FunctionDTOBuilder functionDTOBuilder;

    @Autowired
    FunctionEntityBuilder functionEntityBuilder;

    @Autowired
    FunctionEntityFilter functionEntityFilter;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    RunRepository runRepository;

    @Override
    public Function createFunction(String projectName, Function functionDTO) {
        try {
            // Check that project context is the same as the project passed to the
            // functionDTO
            if (!projectName.equals(functionDTO.getProject())) {
                throw new CustomException("Project Context and Function Project does not match", null);
            }

            // Check project context
            checkContext(functionDTO.getProject());

            // Check if function already exist if exist throw exception otherwise create a
            // new one
            FunctionEntity function = (FunctionEntity) Optional
                .ofNullable(functionDTO.getId())
                .flatMap(id ->
                    functionRepository
                        .findById(id)
                        .map(a -> {
                            throw new CustomException(
                                "The project already contains an function with the specified UUID.",
                                null
                            );
                        })
                )
                .orElseGet(() -> {
                    // Build an function and store it in the database
                    FunctionEntity newFunction = functionEntityBuilder.build(functionDTO);
                    return functionRepository.saveAndFlush(newFunction);
                });

            // Return function DTO
            return functionDTOBuilder.build(function, false);
        } catch (CustomException e) {
            throw new CoreException(
                ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    public Function getByUuid(String projectName, String uuid) {
        checkContext(projectName);

        return functionRepository
            .findById(uuid)
            .map(function -> {
                try {
                    return functionDTOBuilder.build(function, false);
                } catch (CustomException e) {
                    throw new CoreException(
                        ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                        e.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR
                    );
                }
            })
            .orElseThrow(() ->
                new CoreException(
                    ErrorList.FUNCTION_NOT_FOUND.getValue(),
                    ErrorList.FUNCTION_NOT_FOUND.getReason(),
                    HttpStatus.NOT_FOUND
                )
            );
    }

    @Override
    public Page<Function> getLatestByProjectName(Map<String, String> filter, String projectName, Pageable pageable) {
        try {
            checkContext(projectName);

            functionEntityFilter.setCreatedDate(filter.get("created"));
            functionEntityFilter.setName(filter.get("name"));
            functionEntityFilter.setKind(filter.get("kind"));

            Optional<State> stateOptional = Stream
                .of(State.values())
                .filter(state -> state.name().equals(filter.get("state")))
                .findAny();

            functionEntityFilter.setState(stateOptional.map(Enum::name).orElse(null));

            Specification<FunctionEntity> specification = createSpecification(filter, functionEntityFilter)
                .and(CommonSpecification.latestByProject(projectName));

            Page<FunctionEntity> functionPage = functionRepository.findAll(
                Specification
                    .where(specification)
                    .and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("project"), projectName)),
                pageable
            );

            return new PageImpl<>(
                functionPage
                    .getContent()
                    .stream()
                    .map(function -> functionDTOBuilder.build(function, false))
                    .collect(Collectors.toList()),
                pageable,
                functionPage.getTotalElements()
            );
        } catch (CustomException e) {
            throw new CoreException(
                ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    public Page<Function> getByProjectNameAndFunctionName(
        Map<String, String> filter,
        String projectName,
        String functionName,
        Pageable pageable
    ) {
        try {
            checkContext(projectName);

            functionEntityFilter.setCreatedDate(filter.get("created"));
            functionEntityFilter.setKind(filter.get("kind"));
            Optional<State> stateOptional = Stream
                .of(State.values())
                .filter(state -> state.name().equals(filter.get("state")))
                .findAny();

            functionEntityFilter.setState(stateOptional.map(Enum::name).orElse(null));

            Specification<FunctionEntity> specification = createSpecification(filter, functionEntityFilter);

            Page<FunctionEntity> functionPage = functionRepository.findAll(
                Specification
                    .where(specification)
                    .and((root, query, criteriaBuilder) ->
                        criteriaBuilder.and(
                            criteriaBuilder.equal(root.get("project"), projectName),
                            criteriaBuilder.equal(root.get("name"), functionName)
                        )
                    ),
                pageable
            );

            return new PageImpl<>(
                functionPage
                    .getContent()
                    .stream()
                    .map(function -> functionDTOBuilder.build(function, false))
                    .collect(Collectors.toList()),
                pageable,
                functionPage.getTotalElements()
            );
        } catch (CustomException e) {
            throw new CoreException(
                ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    public List<Function> listByProjectNameAndFunctionName(String projectName, String functionName) {
        try {
            checkContext(projectName);

            return this.functionRepository.findAllByProjectAndNameOrderByCreatedDesc(projectName, functionName)
                .stream()
                .map(f -> functionDTOBuilder.build(f, false))
                .toList();
        } catch (CustomException e) {
            throw new CoreException(
                ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    public Function getByProjectAndFunctionAndUuid(String projectName, String functionName, String uuid) {
        try {
            // Check project context
            checkContext(projectName);

            return this.functionRepository.findByProjectAndNameAndId(projectName, functionName, uuid)
                .map(function -> functionDTOBuilder.build(function, false))
                .orElseThrow(() -> new CustomException(ErrorList.FUNCTION_NOT_FOUND.getReason(), null));
        } catch (CustomException e) {
            throw new CoreException(
                ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    public Function getLatestByProjectNameAndFunctionName(String projectName, String functionName) {
        try {
            // Check project context
            checkContext(projectName);

            return this.functionRepository.findLatestFunctionByProjectAndName(projectName, functionName)
                .map(function -> functionDTOBuilder.build(function, false))
                .orElseThrow(() -> new CustomException(ErrorList.FUNCTION_NOT_FOUND.getReason(), null));
        } catch (CustomException e) {
            throw new CoreException(
                ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    public Function createOrUpdateFunction(String projectName, String functionName, Function functionDTO) {
        try {
            // Check that project context is the same as the project passed to the
            // functionDTO
            if (!projectName.equals(functionDTO.getProject())) {
                throw new CustomException("Project Context and Function Project does not match.", null);
            }
            if (!functionName.equals(functionDTO.getName())) {
                throw new CustomException(
                    "Trying to create/update an function with name different from the one passed in the request.",
                    null
                );
            }

            // Check project context
            checkContext(functionDTO.getProject());

            // Check if function already exist if exist throw exception otherwise create a
            // new one
            FunctionEntity function = Optional
                .ofNullable(functionDTO.getId())
                .flatMap(id -> {
                    Optional<FunctionEntity> optionalFunction = functionRepository.findById(id);
                    if (optionalFunction.isPresent()) {
                        FunctionEntity existingFunction = optionalFunction.get();

                        // Update the existing function version
                        final FunctionEntity functionUpdated = functionEntityBuilder.update(
                            existingFunction,
                            functionDTO
                        );
                        return Optional.of(this.functionRepository.saveAndFlush(functionUpdated));
                    } else {
                        // Build a new function and store it in the database
                        FunctionEntity newFunction = functionEntityBuilder.build(functionDTO);
                        return Optional.of(functionRepository.saveAndFlush(newFunction));
                    }
                })
                .orElseGet(() -> {
                    // Build a new function and store it in the database
                    FunctionEntity newFunction = functionEntityBuilder.build(functionDTO);
                    return functionRepository.saveAndFlush(newFunction);
                });

            // Return function DTO
            return functionDTOBuilder.build(function, false);
        } catch (CustomException e) {
            throw new CoreException(
                ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    public Function updateFunction(String projectName, String functionName, String uuid, Function functionDTO) {
        try {
            // Check that project context is the same as the project passed to the
            // functionDTO
            if (!projectName.equals(functionDTO.getProject())) {
                throw new CustomException("Project Context and Function Project does not match", null);
            }
            if (!uuid.equals(functionDTO.getId())) {
                throw new CustomException(
                    "Trying to update an function with an ID different from the one passed in the request.",
                    null
                );
            }
            // Check project context
            checkContext(functionDTO.getProject());

            FunctionEntity function =
                this.functionRepository.findById(functionDTO.getId())
                    .map(a -> // Update the existing function version
                        functionEntityBuilder.update(a, functionDTO)
                    )
                    .orElseThrow(() -> new CustomException("The function does not exist.", null));

            // Return function DTO
            return functionDTOBuilder.build(function, false);
        } catch (CustomException e) {
            throw new CoreException(
                ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    @Transactional
    public Boolean deleteSpecificFunctionVersion(String projectName, String functionName, String uuid) {
        try {
            if (this.functionRepository.existsByProjectAndNameAndId(projectName, functionName, uuid)) {
                // Cascade deletion of all dependencies
                Function function = getByProjectAndFunctionAndUuid(projectName, functionName, uuid);

                // Remove Task
                List<TaskEntity> taskList = this.taskRepository.findByFunction(TaskUtils.buildTaskString(function));
                //Delete all related object
                taskList.forEach(task -> {
                    // remove run
                    this.runRepository.deleteByTaskId(task.getId());

                    // remove task
                    this.taskRepository.deleteById(task.getId());
                });

                this.functionRepository.deleteByProjectAndNameAndId(projectName, functionName, uuid);

                return true;
            }
            throw new CoreException(
                ErrorList.FUNCTION_NOT_FOUND.getValue(),
                ErrorList.FUNCTION_NOT_FOUND.getReason(),
                HttpStatus.NOT_FOUND
            );
        } catch (Exception e) {
            throw new CoreException(
                ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                "cannot delete function",
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    @Transactional
    public Boolean deleteAllFunctionVersions(String projectName, String functionName) {
        try {
            if (functionRepository.existsByProjectAndName(projectName, functionName)) {
                // Cascade delete
                listByProjectNameAndFunctionName(projectName, functionName)
                    .forEach(function -> {
                        // Remove Task
                        List<TaskEntity> taskList =
                            this.taskRepository.findByFunction(TaskUtils.buildTaskString(function));
                        //Delete all related object
                        taskList.forEach(task -> {
                            // remove run
                            this.runRepository.deleteByTaskId(task.getId());

                            // remove task
                            this.taskRepository.deleteById(task.getId());
                        });
                    });

                this.functionRepository.deleteByProjectAndName(projectName, functionName);

                return true;
            }
            throw new CoreException(
                ErrorList.FUNCTION_NOT_FOUND.getValue(),
                ErrorList.FUNCTION_NOT_FOUND.getReason(),
                HttpStatus.NOT_FOUND
            );
        } catch (Exception e) {
            throw new CoreException(
                ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                "cannot delete function",
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
