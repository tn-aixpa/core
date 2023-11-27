package it.smartcommunitylabdhub.core.services;

import it.smartcommunitylabdhub.core.exceptions.CoreException;
import it.smartcommunitylabdhub.core.exceptions.CustomException;
import it.smartcommunitylabdhub.core.models.accessors.utils.RunUtils;
import it.smartcommunitylabdhub.core.models.accessors.utils.TaskUtils;
import it.smartcommunitylabdhub.core.models.builders.task.TaskDTOBuilder;
import it.smartcommunitylabdhub.core.models.builders.workflow.WorkflowDTOBuilder;
import it.smartcommunitylabdhub.core.models.builders.workflow.WorkflowEntityBuilder;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.entities.run.RunEntity;
import it.smartcommunitylabdhub.core.models.entities.run.XRun;
import it.smartcommunitylabdhub.core.models.entities.workflow.Workflow;
import it.smartcommunitylabdhub.core.models.entities.workflow.WorkflowDTO;
import it.smartcommunitylabdhub.core.repositories.RunRepository;
import it.smartcommunitylabdhub.core.repositories.TaskRepository;
import it.smartcommunitylabdhub.core.repositories.WorkflowRepository;
import it.smartcommunitylabdhub.core.services.interfaces.WorkflowService;
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
public class WorkflowServiceImpl implements WorkflowService {

    @Autowired
    WorkflowRepository workflowRepository;

    @Autowired
    RunRepository runRepository;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    WorkflowEntityBuilder workflowEntityBuilder;

    @Autowired
    WorkflowDTOBuilder workflowDTOBuilder;

    @Autowired
    TaskDTOBuilder taskDTOBuilder;

    @Override
    public List<WorkflowDTO> getWorkflows(Pageable pageable) {
        try {
            Page<Workflow> workflowPage = this.workflowRepository.findAll(pageable);
            return workflowPage.getContent().stream().map((workflow) -> {
                return workflowDTOBuilder.build(workflow, false);
            }).collect(Collectors.toList());
        } catch (CustomException e) {
            throw new CoreException(
                    "InternalServerError",
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    public WorkflowDTO createWorkflow(WorkflowDTO workflowDTO) {
        if (workflowDTO.getId() != null && workflowRepository.existsById(workflowDTO.getId())) {
            throw new CoreException("DuplicateWorkflowId",
                    "Cannot create the workflow", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        Optional<Workflow> savedWorkflow = Optional.of(workflowDTO)
                .map(workflowEntityBuilder::build)
                .map(this.workflowRepository::save);

        return savedWorkflow.map(workflow -> workflowDTOBuilder.build(workflow, false))
                .orElseThrow(() -> new CoreException(
                        "InternalServerError",
                        "Error saving workflow",
                        HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Override
    public WorkflowDTO getWorkflow(String uuid) {
        return workflowRepository.findById(uuid)
                .map(workflow -> {
                    try {
                        return workflowDTOBuilder.build(workflow, false);
                    } catch (CustomException e) {
                        throw new CoreException(
                                "InternalServerError",
                                e.getMessage(),
                                HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                })
                .orElseThrow(() -> new CoreException(
                        "WorkflowNotFound",
                        "The workflow you are searching for does not exist.",
                        HttpStatus.NOT_FOUND));
    }

    @Override
    public WorkflowDTO updateWorkflow(WorkflowDTO workflowDTO, String uuid) {
        if (!workflowDTO.getId().equals(uuid)) {
            throw new CoreException(
                    "WorkflowNotMatch",
                    "Trying to update a workflow with a UUID different from the one passed in the request.",
                    HttpStatus.NOT_FOUND);
        }

        return workflowRepository.findById(uuid)
                .map(workflow -> {
                    try {
                        Workflow workflowUpdated =
                                workflowEntityBuilder.update(workflow, workflowDTO);
                        workflowRepository.save(workflowUpdated);
                        return workflowDTOBuilder.build(workflowUpdated, false);
                    } catch (CustomException e) {
                        throw new CoreException(
                                "InternalServerError",
                                e.getMessage(),
                                HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                })
                .orElseThrow(() -> new CoreException(
                        "WorkflowNotFound",
                        "The workflow you are searching for does not exist.",
                        HttpStatus.NOT_FOUND));
    }

    @Override
    public boolean deleteWorkflow(String uuid) {
        try {
            if (this.workflowRepository.existsById(uuid)) {
                this.workflowRepository.deleteById(uuid);
                return true;
            }
            throw new CoreException(
                    "WorkflowNotFound",
                    "The workflow you are trying to delete does not exist.",
                    HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            throw new CoreException(
                    "InternalServerError",
                    "cannot delete workflow",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<XRun> getWorkflowRuns(String uuid) {
        final Workflow workflow = workflowRepository.findById(uuid).orElse(null);
        if (workflow == null) {
            throw new CoreException(
                    "WorkflowNotFound",
                    "The workflow you are searching for does not exist.",
                    HttpStatus.NOT_FOUND);
        }

        WorkflowDTO workflowDTO = workflowDTOBuilder.build(workflow, false);

        try {
            List<RunEntity> runs =
                    this.taskRepository.findByFunction(TaskUtils.buildTaskString(workflowDTO))
                            .stream()
                            .flatMap(task -> this.runRepository
                                    .findByTask(RunUtils.buildRunString(workflowDTO,
                                            taskDTOBuilder.build(task)))
                                    .stream())
                            .collect(Collectors.toList());

            return (List<XRun>) ConversionUtils.reverseIterable(runs, "run", XRun.class);

        } catch (CustomException e) {
            throw new CoreException(
                    "InternalServerError",
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
