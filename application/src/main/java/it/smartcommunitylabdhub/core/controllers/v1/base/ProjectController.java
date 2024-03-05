package it.smartcommunitylabdhub.core.controllers.v1.base;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.smartcommunitylabdhub.commons.Keys;
import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.models.entities.project.Project;
import it.smartcommunitylabdhub.commons.models.queries.SearchFilter;
import it.smartcommunitylabdhub.core.ApplicationKeys;
import it.smartcommunitylabdhub.core.annotations.ApiVersion;
import it.smartcommunitylabdhub.core.models.entities.project.ProjectEntity;
import it.smartcommunitylabdhub.core.models.queries.filters.entities.ProjectEntityFilter;
import it.smartcommunitylabdhub.core.models.queries.services.SearchableProjectService;
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
import org.springframework.web.bind.annotation.*;

@RestController
@ApiVersion("v1")
@RequestMapping("/projects")
//TODO evaluate permissions for project via lookup in dto
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Validated
@Slf4j
@Tag(name = "Project base API", description = "Endpoints related to project management")
public class ProjectController {

    @Autowired
    SearchableProjectService projectService;

    @Operation(summary = "List project", description = "Return a list of all projects")
    @GetMapping(path = "", produces = "application/json; charset=UTF-8")
    public Page<Project> getProjects(
        @ParameterObject @RequestParam(required = false) @Valid @Nullable ProjectEntityFilter filter,
        @ParameterObject @PageableDefault(page = 0, size = ApplicationKeys.DEFAULT_PAGE_SIZE) @SortDefault.SortDefaults(
            { @SortDefault(sort = "name", direction = Direction.ASC) }
        ) Pageable pageable
    ) {
        SearchFilter<ProjectEntity> sf = null;
        if (filter != null) {
            sf = filter.toSearchFilter();
        }

        return projectService.searchProjects(pageable, sf);
    }

    @Operation(summary = "Create project", description = "Create an project and return")
    @PostMapping(
        value = "",
        consumes = { MediaType.APPLICATION_JSON_VALUE, "application/x-yaml" },
        produces = "application/json; charset=UTF-8"
    )
    public Project createProject(@RequestBody @Valid @NotNull Project dto) throws DuplicatedEntityException {
        return projectService.createProject(dto);
    }

    @Operation(summary = "Get an project by id", description = "Return an project")
    @GetMapping(path = "/{id}", produces = "application/json; charset=UTF-8")
    public Project getProject(@PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String id)
        throws NoSuchEntityException {
        return projectService.getProject(id);
    }

    @Operation(summary = "Update specific project", description = "Update and return the project")
    @PutMapping(
        path = "/{id}",
        consumes = { MediaType.APPLICATION_JSON_VALUE, "application/x-yaml" },
        produces = "application/json; charset=UTF-8"
    )
    public Project updateProject(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String id,
        @RequestBody @Valid @NotNull Project dto
    ) throws NoSuchEntityException {
        return projectService.updateProject(id, dto);
    }

    @Operation(summary = "Delete a project", description = "Delete a specific project, with optional cascade")
    @DeleteMapping(path = "/{id}")
    public void deleteProject(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String id,
        @RequestParam(required = false) Boolean cascade
    ) {
        projectService.deleteProject(id, cascade);
    }
    // @Operation(summary = "Read project secret data", description = "Get project secrets data for the specified keys")
    // @GetMapping(path = "/{name}/secrets/data", produces = "application/json; charset=UTF-8")
    // public Map<String, String>> projectSecretData(
    //     @ValidateField @PathVariable String name,
    //     @RequestParam Set<String> keys
    // ) {
    //     return projectService.getProjectSecretData(name, keys));
    // }

    // @Operation(summary = "Store project secret data", description = "Store project secrets data")
    // @PutMapping(path = "/{name}/secrets/data", produces = "application/json; charset=UTF-8")
    // public Void> storeProjectSecretData(
    //     @ValidateField @PathVariable String name,
    //     @RequestBody Map<String, String> values
    // ) {
    //     this.projectService.storeProjectSecretData(name, values);
    //     return ResponseEntity.ok().build();
    // }
}
