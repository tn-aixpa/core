package it.smartcommunitylabdhub.core.controllers.v1.base;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.smartcommunitylabdhub.authorization.model.ResourceShareEntity;
import it.smartcommunitylabdhub.authorization.services.ShareableAwareEntityService;
import it.smartcommunitylabdhub.commons.Keys;
import it.smartcommunitylabdhub.commons.config.SecurityProperties;
import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.exceptions.SystemException;
import it.smartcommunitylabdhub.commons.models.entities.project.Project;
import it.smartcommunitylabdhub.commons.models.queries.SearchFilter;
import it.smartcommunitylabdhub.core.ApplicationKeys;
import it.smartcommunitylabdhub.core.annotations.ApiVersion;
import it.smartcommunitylabdhub.core.models.entities.ProjectEntity;
import it.smartcommunitylabdhub.core.models.queries.filters.entities.ProjectEntityFilter;
import it.smartcommunitylabdhub.core.models.queries.services.SearchableProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import javax.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindException;
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
@RequestMapping("/projects")
@PreAuthorize("hasAuthority('ROLE_USER')")
@Validated
@Slf4j
@Tag(name = "Project base API", description = "Endpoints related to project management")
public class ProjectController {

    @Autowired
    SearchableProjectService projectService;

    @Autowired
    ShareableAwareEntityService<Project> shareService;

    @Autowired
    private AuditorAware<String> auditor;

    @Autowired
    SecurityProperties securityProperties;

    @Operation(summary = "List project", description = "Return a list of all projects")
    @GetMapping(path = "", produces = "application/json; charset=UTF-8")
    public Page<Project> getProjects(
        @ParameterObject @Valid @Nullable ProjectEntityFilter filter,
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
    public Project createProject(@RequestBody @Valid @NotNull Project dto)
        throws DuplicatedEntityException, IllegalArgumentException, SystemException, BindException {
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
        @RequestBody @Valid @NotNull Project dto,
        Authentication auth
    ) throws NoSuchEntityException, IllegalArgumentException, SystemException, BindException {
        if (securityProperties.isRequired()) {
            //custom authorization check: only owner or with (scoped) admin role can update
            //TODO move to bean
            if (auth == null || auditor == null) {
                throw new InsufficientAuthenticationException("missing valid authentication");
            }

            String user = auditor
                .getCurrentAuditor()
                .orElseThrow(() -> new InsufficientAuthenticationException("missing valid authentication"));

            Project project = projectService.getProject(id);
            if (
                auth.getAuthorities().stream().noneMatch(a -> "ROLE_ADMIN".equals(a.getAuthority())) &&
                auth.getAuthorities().stream().noneMatch(a -> (id + ":ROLE_ADMIN").equals(a.getAuthority())) &&
                !user.equals(project.getUser())
            ) {
                throw new InsufficientAuthenticationException("current user is not authorized");
            }
        }

        return projectService.updateProject(id, dto);
    }

    @Operation(summary = "Delete a project", description = "Delete a specific project, with optional cascade")
    @DeleteMapping(path = "/{id}")
    public void deleteProject(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String id,
        @RequestParam(required = false) Boolean cascade,
        Authentication auth
    ) {
        if (securityProperties.isRequired()) {
            //custom authorization check: only owner or with (scoped) admin role can delete
            //TODO move to bean
            if (auth == null || auditor == null) {
                throw new InsufficientAuthenticationException("missing valid authentication");
            }

            String user = auditor
                .getCurrentAuditor()
                .orElseThrow(() -> new InsufficientAuthenticationException("missing valid authentication"));

            Project project = projectService.findProject(id);

            if (project != null) {
                if (
                    auth.getAuthorities().stream().noneMatch(a -> "ROLE_ADMIN".equals(a.getAuthority())) &&
                    auth.getAuthorities().stream().noneMatch(a -> (id + ":ROLE_ADMIN").equals(a.getAuthority())) &&
                    !user.equals(project.getUser())
                ) {
                    throw new InsufficientAuthenticationException("current user is not authorized");
                }
            }
        }

        projectService.deleteProject(id, cascade);
    }

    @Operation(summary = "Share a project with a user", description = "Share project")
    @PostMapping(path = "/{id}/share", produces = "application/json; charset=UTF-8")
    public ResourceShareEntity shareProject(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String id,
        @RequestParam @Valid @NotNull String user
    ) throws NoSuchEntityException {
        return shareService.share(id, user);
    }
}
