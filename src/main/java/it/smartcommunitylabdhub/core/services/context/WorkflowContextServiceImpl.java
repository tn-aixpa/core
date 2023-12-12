package it.smartcommunitylabdhub.core.services.context;

import it.smartcommunitylabdhub.core.exceptions.CoreException;
import it.smartcommunitylabdhub.core.exceptions.CustomException;
import it.smartcommunitylabdhub.core.models.builders.workflow.WorkflowDTOBuilder;
import it.smartcommunitylabdhub.core.models.builders.workflow.WorkflowEntityBuilder;
import it.smartcommunitylabdhub.core.models.entities.workflow.Workflow;
import it.smartcommunitylabdhub.core.models.entities.workflow.WorkflowEntity;
import it.smartcommunitylabdhub.core.models.enums.State;
import it.smartcommunitylabdhub.core.models.filters.entities.WorkflowEntityFilter;
import it.smartcommunitylabdhub.core.repositories.WorkflowRepository;
import it.smartcommunitylabdhub.core.services.context.interfaces.WorkflowContextService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class WorkflowContextServiceImpl extends ContextService<WorkflowEntity, WorkflowEntityFilter>
        implements WorkflowContextService {

    @Autowired
    WorkflowRepository workflowRepository;

    @Autowired
    WorkflowEntityBuilder workflowEntityBuilder;

    @Autowired
    WorkflowEntityFilter workflowEntityFilter;

    @Autowired
    WorkflowDTOBuilder workflowDTOBuilder;

    @Override
    public Workflow createWorkflow(String projectName, Workflow workflowDTO) {
        try {
            // Check that project context is the same as the project passed to the
            // workflowDTO
            if (!projectName.equals(workflowDTO.getProject())) {
                throw new CustomException("Project Context and Workflow Project does not match",
                        null);
            }

            // Check project context
            checkContext(workflowDTO.getProject());

            // Check if workflow already exist if exist throw exception otherwise create a
            // new one
            WorkflowEntity workflow = (WorkflowEntity) Optional.ofNullable(workflowDTO.getId())
                    .flatMap(id -> workflowRepository.findById(id)
                            .map(a -> {
                                throw new CustomException(
                                        "The project already contains an workflow with the specified UUID.",
                                        null);
                            }))
                    .orElseGet(() -> {
                        // Build an workflow and store it in the database
                        WorkflowEntity newWorkflow = workflowEntityBuilder.build(workflowDTO);
                        return workflowRepository.saveAndFlush(newWorkflow);
                    });

            // Return workflow DTO
            return workflowDTOBuilder.build(workflow, false);

        } catch (CustomException e) {
            throw new CoreException(
                    "InternalServerError",
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Page<Workflow> getLatestByProjectName(Map<String, String> filter, String projectName, Pageable pageable) {
        try {
            checkContext(projectName);


            workflowEntityFilter.setCreatedDate(filter.get("created"));
            workflowEntityFilter.setName(filter.get("name"));
            workflowEntityFilter.setKind(filter.get("kind"));

            Optional<State> stateOptional = Stream.of(State.values())
                    .filter(state -> state.name().equals(filter.get("state")))
                    .findAny();

            workflowEntityFilter.setState(stateOptional.map(Enum::name).orElse(null));

            Specification<WorkflowEntity> specification = createSpecification(filter, workflowEntityFilter);

            Page<WorkflowEntity> workflowPage = workflowRepository.findAll(
                    Specification.where(specification).and((root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(root.get("project"), projectName)), pageable);


            return new PageImpl<>(
                    workflowPage.getContent()
                            .stream()
                            .map((workflow) -> {
                                return workflowDTOBuilder.build(workflow, false);
                            }).collect(Collectors.toList()),
                    pageable, workflowPage.getContent().size()
            );
        } catch (CustomException e) {
            throw new CoreException(
                    "InternalServerError",
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Page<Workflow> getByProjectNameAndWorkflowName(Map<String, String> filter, String projectName,
                                                          String workflowName,
                                                          Pageable pageable) {
        try {
            checkContext(projectName);

            workflowEntityFilter.setCreatedDate(filter.get("created"));
            workflowEntityFilter.setKind(filter.get("kind"));
            Optional<State> stateOptional = Stream.of(State.values())
                    .filter(state -> state.name().equals(filter.get("state")))
                    .findAny();

            workflowEntityFilter.setState(stateOptional.map(Enum::name).orElse(null));

            Specification<WorkflowEntity> specification = createSpecification(filter, workflowEntityFilter);

            Page<WorkflowEntity> workflowPage = workflowRepository.findAll(
                    Specification.where(specification)
                            .and((root, query, criteriaBuilder) ->
                                    criteriaBuilder.and(
                                            criteriaBuilder.equal(root.get("project"), projectName),
                                            criteriaBuilder.equal(root.get("name"), workflowName))),
                    pageable);

            return new PageImpl<>(
                    workflowPage.getContent()
                            .stream()
                            .map((workflow) -> {
                                return workflowDTOBuilder.build(workflow, false);
                            }).collect(Collectors.toList()),
                    pageable, workflowPage.getContent().size()
            );
        } catch (CustomException e) {
            throw new CoreException(
                    "InternalServerError",
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    public Workflow getByProjectAndWorkflowAndUuid(String projectName, String workflowName,
                                                   String uuid) {
        try {
            // Check project context
            checkContext(projectName);

            return this.workflowRepository
                    .findByProjectAndNameAndId(projectName, workflowName, uuid).map(
                            workflow -> workflowDTOBuilder.build(workflow, false))
                    .orElseThrow(
                            () -> new CustomException("The workflow does not exist.", null));

        } catch (CustomException e) {
            throw new CoreException(
                    "InternalServerError",
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Workflow getLatestByProjectNameAndWorkflowName(String projectName,
                                                          String workflowName) {
        try {
            // Check project context
            checkContext(projectName);

            return this.workflowRepository
                    .findLatestWorkflowByProjectAndName(projectName, workflowName).map(
                            workflow -> workflowDTOBuilder.build(workflow, false))
                    .orElseThrow(
                            () -> new CustomException("The workflow does not exist.", null));

        } catch (CustomException e) {
            throw new CoreException(
                    "InternalServerError",
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Workflow createOrUpdateWorkflow(String projectName, String workflowName,
                                           Workflow workflowDTO) {
        try {
            // Check that project context is the same as the project passed to the
            // workflowDTO
            if (!projectName.equals(workflowDTO.getProject())) {
                throw new CustomException("Project Context and Workflow Project does not match.",
                        null);
            }
            if (!workflowName.equals(workflowDTO.getName())) {
                throw new CustomException(
                        "Trying to create/update an workflow with name different from the one passed in the request.",
                        null);
            }

            // Check project context
            checkContext(workflowDTO.getProject());

            // Check if workflow already exist if exist throw exception otherwise create a
            // new one
            WorkflowEntity workflow = Optional.ofNullable(workflowDTO.getId())
                    .flatMap(id -> {
                        Optional<WorkflowEntity> optionalWorkflow = workflowRepository.findById(id);
                        if (optionalWorkflow.isPresent()) {
                            WorkflowEntity existingWorkflow = optionalWorkflow.get();

                            // Update the existing workflow version
                            final WorkflowEntity workflowUpdated =
                                    workflowEntityBuilder.update(existingWorkflow,
                                            workflowDTO);
                            return Optional.of(this.workflowRepository.saveAndFlush(workflowUpdated));

                        } else {
                            // Build a new workflow and store it in the database
                            WorkflowEntity newWorkflow = workflowEntityBuilder.build(workflowDTO);
                            return Optional.of(workflowRepository.saveAndFlush(newWorkflow));
                        }
                    })
                    .orElseGet(() -> {
                        // Build a new workflow and store it in the database
                        WorkflowEntity newWorkflow = workflowEntityBuilder.build(workflowDTO);
                        return workflowRepository.saveAndFlush(newWorkflow);
                    });

            // Return workflow DTO
            return workflowDTOBuilder.build(workflow, false);

        } catch (CustomException e) {
            throw new CoreException(
                    "InternalServerError",
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Workflow updateWorkflow(String projectName, String workflowName, String uuid,
                                   Workflow workflowDTO) {

        try {
            // Check that project context is the same as the project passed to the
            // workflowDTO
            if (!projectName.equals(workflowDTO.getProject())) {
                throw new CustomException("Project Context and Workflow Project does not match",
                        null);
            }
            if (!uuid.equals(workflowDTO.getId())) {
                throw new CustomException(
                        "Trying to update an workflow with an ID different from the one passed in the request.",
                        null);
            }
            // Check project context
            checkContext(workflowDTO.getProject());

            WorkflowEntity workflow = this.workflowRepository.findById(workflowDTO.getId()).map(
                            a -> {
                                // Update the existing workflow version
                                return workflowEntityBuilder.update(a, workflowDTO);
                            })
                    .orElseThrow(
                            () -> new CustomException("The workflow does not exist.", null));

            // Return workflow DTO
            return workflowDTOBuilder.build(workflow, false);

        } catch (CustomException e) {
            throw new CoreException(
                    "InternalServerError",
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public Boolean deleteSpecificWorkflowVersion(String projectName, String workflowName,
                                                 String uuid) {
        try {
            if (this.workflowRepository.existsByProjectAndNameAndId(projectName, workflowName,
                    uuid)) {
                this.workflowRepository.deleteByProjectAndNameAndId(projectName, workflowName,
                        uuid);
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
    @Transactional
    public Boolean deleteAllWorkflowVersions(String projectName, String workflowName) {
        try {
            if (workflowRepository.existsByProjectAndName(projectName, workflowName)) {
                this.workflowRepository.deleteByProjectAndName(projectName, workflowName);
                return true;
            }
            throw new CoreException(
                    "WorkflowNotFound",
                    "The workflows you are trying to delete does not exist.",
                    HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            throw new CoreException(
                    "InternalServerError",
                    "cannot delete workflow",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
