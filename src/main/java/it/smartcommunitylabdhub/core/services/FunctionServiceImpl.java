package it.smartcommunitylabdhub.core.services;

import it.smartcommunitylabdhub.core.exceptions.CoreException;
import it.smartcommunitylabdhub.core.exceptions.CustomException;
import it.smartcommunitylabdhub.core.models.accessors.utils.RunUtils;
import it.smartcommunitylabdhub.core.models.accessors.utils.TaskUtils;
import it.smartcommunitylabdhub.core.models.builders.function.FunctionDTOBuilder;
import it.smartcommunitylabdhub.core.models.builders.function.FunctionEntityBuilder;
import it.smartcommunitylabdhub.core.models.builders.task.TaskDTOBuilder;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.entities.function.Function;
import it.smartcommunitylabdhub.core.models.entities.function.FunctionEntity;
import it.smartcommunitylabdhub.core.models.entities.run.RunEntity;
import it.smartcommunitylabdhub.core.models.entities.run.Run;
import it.smartcommunitylabdhub.core.repositories.FunctionRepository;
import it.smartcommunitylabdhub.core.repositories.RunRepository;
import it.smartcommunitylabdhub.core.repositories.TaskRepository;
import it.smartcommunitylabdhub.core.services.interfaces.FunctionService;
import it.smartcommunitylabdhub.core.utils.ErrorList;
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
public class FunctionServiceImpl implements FunctionService {

    @Autowired
    FunctionRepository functionRepository;

    @Autowired
    RunRepository runRepository;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    FunctionDTOBuilder functionDTOBuilder;

    @Autowired
    FunctionEntityBuilder functionEntityBuilder;

    @Autowired
    TaskDTOBuilder taskDTOBuilder;

    @Override
    public List<Function> getFunctions(Pageable pageable) {
        try {
            Page<FunctionEntity> functionPage = this.functionRepository.findAll(pageable);
            return functionPage.getContent().stream()
                    .map(function -> functionDTOBuilder.build(function, false))
                    .collect(Collectors.toList());
        } catch (CustomException e) {
            throw new CoreException(
                    ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    public List<Function> getFunctions() {
        try {
            List<FunctionEntity> functions = this.functionRepository.findAll();
            return functions.stream().map(function -> functionDTOBuilder.build(function, false))
                    .collect(Collectors.toList());
        } catch (CustomException e) {
            throw new CoreException(
                    ErrorList.FUNCTION_NOT_FOUND.getValue(),
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Function createFunction(Function functionDTO) {
        if (functionDTO.getId() != null && functionRepository.existsById(functionDTO.getId())) {
            throw new CoreException(
                    ErrorList.DUPLICATE_FUNCTION.getValue(),
                    ErrorList.DUPLICATE_FUNCTION.getReason(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        Optional<FunctionEntity> savedFunction = Optional.ofNullable(functionDTO)
                .map(functionEntityBuilder::build)
                .map(this.functionRepository::save);

        return savedFunction.map(function -> functionDTOBuilder.build(function, false))
                .orElseThrow(() -> new CoreException(
                        ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                        "Error saving function",
                        HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Override
    public Function getFunction(String uuid) {

        final FunctionEntity function = functionRepository.findById(uuid).orElse(null);
        if (function == null) {
            throw new CoreException(
                    ErrorList.FUNCTION_NOT_FOUND.getValue(),
                    ErrorList.FUNCTION_NOT_FOUND.getReason(),
                    HttpStatus.NOT_FOUND);
        }

        try {
            return functionDTOBuilder.build(function, false);

        } catch (CustomException e) {
            throw new CoreException(
                    ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Function updateFunction(Function functionDTO, String uuid) {

        if (!functionDTO.getId().equals(uuid)) {
            throw new CoreException(
                    ErrorList.FUNCTION_NOT_MATCH.getValue(),
                    ErrorList.FUNCTION_NOT_MATCH.getReason(),
                    HttpStatus.NOT_FOUND);
        }

        final FunctionEntity function = functionRepository.findById(uuid).orElse(null);
        if (function == null) {
            throw new CoreException(
                    ErrorList.FUNCTION_NOT_FOUND.getValue(),
                    ErrorList.FUNCTION_NOT_FOUND.getReason(),
                    HttpStatus.NOT_FOUND);
        }

        try {

            final FunctionEntity functionUpdated = functionEntityBuilder.update(function, functionDTO);
            this.functionRepository.save(functionUpdated);

            return functionDTOBuilder.build(functionUpdated, false);

        } catch (CustomException e) {
            throw new CoreException(
                    ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public boolean deleteFunction(String uuid) {
        try {
            if (this.functionRepository.existsById(uuid)) {
                this.functionRepository.deleteById(uuid);
                return true;
            }
            throw new CoreException(
                    ErrorList.FUNCTION_NOT_FOUND.getValue(),
                    ErrorList.FUNCTION_NOT_FOUND.getReason(),
                    HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            throw new CoreException(
                    ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                    "Cannot delete function",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<Run> getFunctionRuns(String uuid) {
        final FunctionEntity function = functionRepository.findById(uuid).orElse(null);
        if (function == null) {
            throw new CoreException(
                    ErrorList.FUNCTION_NOT_FOUND.getValue(),
                    ErrorList.FUNCTION_NOT_FOUND.getReason(),
                    HttpStatus.NOT_FOUND);
        }

        Function functionDTO = functionDTOBuilder.build(function, false);
        try {
            // Find and collect runs for a function
            List<RunEntity> runs =
                    this.taskRepository.findByFunction(TaskUtils.buildTaskString(functionDTO))
                            .stream()
                            .flatMap(task -> this.runRepository
                                    .findByTask(
                                            RunUtils.buildRunString(
                                                    functionDTO,
                                                    taskDTOBuilder.build(task)))
                                    .stream())
                            .collect(Collectors.toList());


            return (List<Run>) ConversionUtils.reverseIterable(runs, "run", Run.class);

        } catch (CustomException e) {
            throw new CoreException(
                    ErrorList.FUNCTION_NOT_FOUND.getValue(),
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<Function> getAllLatestFunctions() {
        try {

            List<FunctionEntity> functionList = this.functionRepository.findAllLatestFunctions();
            return functionList
                    .stream()
                    .map(function -> functionDTOBuilder.build(function, false))
                    .collect(Collectors.toList());
        } catch (CustomException e) {
            throw new CoreException(
                    ErrorList.FUNCTION_NOT_FOUND.getValue(),
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
