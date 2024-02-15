package it.smartcommunitylabdhub.core.services;

import it.smartcommunitylabdhub.commons.exceptions.CoreException;
import it.smartcommunitylabdhub.commons.exceptions.CustomException;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.entities.workflow.Workflow;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.models.utils.RunUtils;
import it.smartcommunitylabdhub.commons.models.utils.TaskUtils;
import it.smartcommunitylabdhub.commons.services.WorkflowService;
import it.smartcommunitylabdhub.core.models.builders.run.RunDTOBuilder;
import it.smartcommunitylabdhub.core.models.builders.task.TaskDTOBuilder;
import it.smartcommunitylabdhub.core.models.builders.workflow.WorkflowDTOBuilder;
import it.smartcommunitylabdhub.core.models.builders.workflow.WorkflowEntityBuilder;
import it.smartcommunitylabdhub.core.models.entities.run.RunEntity;
import it.smartcommunitylabdhub.core.models.entities.workflow.WorkflowEntity;
import it.smartcommunitylabdhub.core.models.queries.filters.abstracts.AbstractSpecificationService;
import it.smartcommunitylabdhub.core.models.queries.filters.entities.WorkflowEntityFilter;
import it.smartcommunitylabdhub.core.repositories.RunRepository;
import it.smartcommunitylabdhub.core.repositories.TaskRepository;
import it.smartcommunitylabdhub.core.repositories.WorkflowRepository;
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
public class WorkflowServiceImpl
    extends AbstractSpecificationService<WorkflowEntity, WorkflowEntityFilter>
    implements WorkflowService {

    @Autowired
    WorkflowRepository workflowRepository;

    @Autowired
    RunRepository runRepository;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    WorkflowEntityBuilder workflowEntityBuilder;

    @Autowired
    WorkflowEntityFilter workflowEntityFilter;

    @Autowired
    WorkflowDTOBuilder workflowDTOBuilder;

    @Autowired
    TaskDTOBuilder taskDTOBuilder;

    @Autowired
    RunDTOBuilder runDTOBuilder;

    @Override
    public Page<Workflow> getWorkflows(Map<String, String> filter, Pageable pageable) {
        try {
            workflowEntityFilter.setCreatedDate(filter.get("created"));
            workflowEntityFilter.setName(filter.get("name"));
            workflowEntityFilter.setKind(filter.get("kind"));

            Optional<State> stateOptional = Stream
                .of(State.values())
                .filter(state -> state.name().equals(filter.get("state")))
                .findAny();

            workflowEntityFilter.setState(stateOptional.map(Enum::name).orElse(null));

            Specification<WorkflowEntity> specification = createSpecification(filter, workflowEntityFilter);

            Page<WorkflowEntity> workflowPage = this.workflowRepository.findAll(specification, pageable);

            return new PageImpl<>(
                workflowPage
                    .getContent()
                    .stream()
                    .map(workflow -> workflowDTOBuilder.build(workflow, false))
                    .collect(Collectors.toList()),
                pageable,
                workflowPage.getTotalElements()
            );
        } catch (CustomException e) {
            throw new CoreException("InternalServerError", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Workflow createWorkflow(Workflow workflowDTO) {
        if (workflowDTO.getId() != null && workflowRepository.existsById(workflowDTO.getId())) {
            throw new CoreException(
                "DuplicateWorkflowId",
                "Cannot create the workflow",
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
        Optional<WorkflowEntity> savedWorkflow = Optional
            .of(workflowDTO)
            .map(workflowEntityBuilder::build)
            .map(this.workflowRepository::saveAndFlush);

        return savedWorkflow
            .map(workflow -> workflowDTOBuilder.build(workflow, false))
            .orElseThrow(() ->
                new CoreException("InternalServerError", "Error saving workflow", HttpStatus.INTERNAL_SERVER_ERROR)
            );
    }

    @Override
    public Workflow getWorkflow(String uuid) {
        return workflowRepository
            .findById(uuid)
            .map(workflow -> {
                try {
                    return workflowDTOBuilder.build(workflow, false);
                } catch (CustomException e) {
                    throw new CoreException("InternalServerError", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            })
            .orElseThrow(() ->
                new CoreException(
                    "WorkflowNotFound",
                    "The workflow you are searching for does not exist.",
                    HttpStatus.NOT_FOUND
                )
            );
    }

    @Override
    public Workflow updateWorkflow(Workflow workflowDTO, String uuid) {
        if (!workflowDTO.getId().equals(uuid)) {
            throw new CoreException(
                "WorkflowNotMatch",
                "Trying to update a workflow with a UUID different from the one passed in the request.",
                HttpStatus.NOT_FOUND
            );
        }

        return workflowRepository
            .findById(uuid)
            .map(workflow -> {
                try {
                    WorkflowEntity workflowUpdated = workflowEntityBuilder.update(workflow, workflowDTO);
                    workflowRepository.saveAndFlush(workflowUpdated);
                    return workflowDTOBuilder.build(workflowUpdated, false);
                } catch (CustomException e) {
                    throw new CoreException("InternalServerError", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            })
            .orElseThrow(() ->
                new CoreException(
                    "WorkflowNotFound",
                    "The workflow you are searching for does not exist.",
                    HttpStatus.NOT_FOUND
                )
            );
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
                HttpStatus.NOT_FOUND
            );
        } catch (Exception e) {
            throw new CoreException("InternalServerError", "cannot delete workflow", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<Run> getWorkflowRuns(String uuid) {
        final WorkflowEntity workflow = workflowRepository.findById(uuid).orElse(null);
        if (workflow == null) {
            throw new CoreException(
                "WorkflowNotFound",
                "The workflow you are searching for does not exist.",
                HttpStatus.NOT_FOUND
            );
        }

        Workflow workflowDTO = workflowDTOBuilder.build(workflow, false);

        try {
            List<RunEntity> runs =
                this.taskRepository.findByFunction(TaskUtils.buildTaskString(workflowDTO))
                    .stream()
                    .flatMap(task ->
                        this.runRepository.findByTask(RunUtils.buildRunString(workflowDTO, taskDTOBuilder.build(task)))
                            .stream()
                    )
                    .collect(Collectors.toList());
            return runs.stream().map(r -> runDTOBuilder.convert(r)).collect(Collectors.toList());
        } catch (CustomException e) {
            throw new CoreException("InternalServerError", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
