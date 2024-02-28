package it.smartcommunitylabdhub.core.controllers.v1.context;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.smartcommunitylabdhub.commons.Keys;
import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.models.entities.workflow.Workflow;
import it.smartcommunitylabdhub.commons.models.queries.SearchFilter;
import it.smartcommunitylabdhub.core.ApplicationKeys;
import it.smartcommunitylabdhub.core.annotations.ApiVersion;
import it.smartcommunitylabdhub.core.models.entities.workflow.WorkflowEntity;
import it.smartcommunitylabdhub.core.models.queries.filters.entities.WorkflowEntityFilter;
import it.smartcommunitylabdhub.core.models.queries.services.SearchableWorkflowService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import javax.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ApiVersion("v1")
@RequestMapping("/-/{project}/workflows")
@PreAuthorize(
    "hasAuthority('ROLE_ADMIN') or (hasAuthority(#project+':ROLE_USER') or hasAuthority(#project+':ROLE_ADMIN'))"
)
@Validated
@Slf4j
@Tag(name = "Workflow context API", description = "Endpoints related to workflows management for project")
public class WorkflowContextController {

    @Autowired
    SearchableWorkflowService workflowService;

    @Operation(
        summary = "Create a workflow in a project context",
        description = "create the workflow for the project (context)"
    )
    @PostMapping(
        value = "",
        consumes = { MediaType.APPLICATION_JSON_VALUE, "application/x-yaml" },
        produces = "application/json; charset=UTF-8"
    )
    public Workflow createWorkflow(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @Valid @NotNull @RequestBody Workflow dto
    ) throws DuplicatedEntityException {
        //enforce project match
        dto.setProject(project);

        String name = dto.getName();

        try {
            @SuppressWarnings("unused")
            Workflow workflow = workflowService.getLatestWorkflow(project, name);

            //there is already an entity with the same name
            throw new DuplicatedEntityException(name);
        } catch (NoSuchEntityException e) {
            //create as new
            return workflowService.createWorkflow(dto);
        }
    }

    @Operation(
        summary = "Retrieve only the latest version of all workflows",
        description = "return a list of the latest version of each workflow related to a project"
    )
    @GetMapping(path = "", produces = "application/json; charset=UTF-8")
    public Page<Workflow> getLatestWorkflows(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @ParameterObject @RequestParam(required = false) @Valid @Nullable WorkflowEntityFilter filter,
        @ParameterObject @PageableDefault(page = 0, size = ApplicationKeys.DEFAULT_PAGE_SIZE) @SortDefault.SortDefaults(
            { @SortDefault(sort = "created", direction = Direction.DESC) }
        ) Pageable pageable
    ) {
        SearchFilter<WorkflowEntity> sf = null;
        if (filter != null) {
            sf = filter.toSearchFilter();
        }

        return workflowService.searchLatestWorkflowsByProject(project, pageable, sf);
    }

    @Operation(
        summary = "Retrieve all versions of the workflow sort by creation",
        description = "return a list of all version of the workflow sort by creation"
    )
    @GetMapping(path = "/{name}", produces = "application/json; charset=UTF-8")
    public Page<Workflow> getAllWorkflows(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String name,
        @ParameterObject @PageableDefault(page = 0, size = ApplicationKeys.DEFAULT_PAGE_SIZE) @SortDefault.SortDefaults(
            { @SortDefault(sort = "created", direction = Direction.DESC) }
        ) Pageable pageable
    ) {
        return workflowService.findWorkflows(project, name, pageable);
    }

    @Operation(
        summary = "Create a new version of a workflow in a project context",
        description = "if workflow exist create a new version of the workflow"
    )
    @PostMapping(
        value = "/{name}",
        consumes = { MediaType.APPLICATION_JSON_VALUE, "application/x-yaml" },
        produces = "application/json; charset=UTF-8"
    )
    public Workflow createOrUpdateWorkflow(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String name,
        @RequestBody @Valid @NotNull Workflow dto
    ) throws NoSuchEntityException, DuplicatedEntityException {
        //enforce project match
        dto.setProject(project);
        dto.setName(name);

        @SuppressWarnings("unused")
        Workflow workflow = workflowService.getLatestWorkflow(project, name);
        workflow = workflowService.createWorkflow(dto);

        return workflow;
    }

    @Operation(
        summary = "Delete all version of a workflow",
        description = "First check if project exist, then delete a specific workflow version"
    )
    @DeleteMapping(path = "/{name}")
    public void deleteWorkflow(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String name
    ) {
        workflowService.deleteWorkflows(project, name);
    }

    /*
     * Versions
     */

    @Operation(
        summary = "Retrieve the latest version of a workflow",
        description = "return the latest version of a workflow"
    )
    @GetMapping(path = "/{name}/latest", produces = "application/json; charset=UTF-8")
    public Workflow getLatestWorkflowByName(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String name
    ) throws NoSuchEntityException {
        return workflowService.getLatestWorkflow(project, name);
    }

    @Operation(
        summary = "Retrieve a specific workflow version given the workflow id",
        description = "return a specific version of the workflow identified by the id"
    )
    @GetMapping(path = "/{name}/{id}", produces = "application/json; charset=UTF-8")
    public Workflow getWorkflowById(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String name,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String id
    ) throws NoSuchEntityException {
        Workflow workflow = workflowService.getWorkflow(id);

        //check for project and name match
        if (!workflow.getProject().equals(project) || !workflow.getName().equals(name)) {
            throw new IllegalArgumentException("invalid project or name");
        }

        return workflow;
    }

    @Operation(
        summary = "Update if exist a workflow in a project context",
        description = "First check if project exist, if workflow exist update."
    )
    @PutMapping(
        value = "/{name}/{id}",
        consumes = { MediaType.APPLICATION_JSON_VALUE, "application/x-yaml" },
        produces = "application/json; charset=UTF-8"
    )
    public Workflow updateWorkflow(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String name,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String id,
        @RequestBody @Valid @NotNull Workflow workflowDTO
    ) throws NoSuchEntityException {
        Workflow workflow = workflowService.getWorkflow(id);

        //check for project and name match
        if (!workflow.getProject().equals(project) || !workflow.getName().equals(name)) {
            throw new IllegalArgumentException("invalid project or name");
        }

        return workflowService.updateWorkflow(id, workflowDTO);
    }

    @Operation(
        summary = "Delete a specific workflow version",
        description = "First check if project exist, then delete a specific workflow version"
    )
    @DeleteMapping(path = "/{name}/{id}")
    public void deleteWorkflow(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String name,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String id
    ) throws NoSuchEntityException {
        Workflow workflow = workflowService.getWorkflow(id);

        //check for project and name match
        if (!workflow.getProject().equals(project) || !workflow.getName().equals(name)) {
            throw new IllegalArgumentException("invalid project or name");
        }

        workflowService.deleteWorkflow(id);
    }
}
