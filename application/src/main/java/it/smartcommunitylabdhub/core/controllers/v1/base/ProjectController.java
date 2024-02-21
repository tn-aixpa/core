package it.smartcommunitylabdhub.core.controllers.v1.base;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.smartcommunitylabdhub.commons.annotations.validators.ValidateField;
import it.smartcommunitylabdhub.commons.models.entities.artifact.Artifact;
import it.smartcommunitylabdhub.commons.models.entities.function.Function;
import it.smartcommunitylabdhub.commons.models.entities.project.Project;
import it.smartcommunitylabdhub.commons.models.entities.secret.Secret;
import it.smartcommunitylabdhub.commons.models.entities.workflow.Workflow;
import it.smartcommunitylabdhub.commons.services.ProjectService;
import it.smartcommunitylabdhub.core.annotations.ApiVersion;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projects")
@ApiVersion("v1")
@Validated
@Slf4j
@Tag(name = "Project base API", description = "Endpoints related to project management")
public class ProjectController {

    @Autowired
    ProjectService projectService;

    @Operation(summary = "List project", description = "Return a list of all projects")
    @GetMapping(path = "", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Page<Project>> getProjects(@RequestParam Map<String, String> filter, Pageable pageable) {
        return ResponseEntity.ok(this.projectService.getProjects(filter, pageable));
    }

    @Operation(summary = "Create project", description = "Create an project and return")
    @PostMapping(
        value = "",
        consumes = { MediaType.APPLICATION_JSON_VALUE, "application/x-yaml" },
        produces = "application/json; charset=UTF-8"
    )
    public ResponseEntity<Project> createProject(@Valid @RequestBody Project projectDTO) {
        return ResponseEntity.ok(this.projectService.createProject(projectDTO));
    }

    @Operation(summary = "Get an project by name", description = "Return an project")
    @GetMapping(path = "/{name}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Project> getProject(
        @ValidateField @PathVariable(name = "name", required = true) String name
    ) {
        return ResponseEntity.ok(this.projectService.getProject(name));
    }

    @Operation(summary = "Update specific project", description = "Update and return the project")
    @PutMapping(
        path = "/{name}",
        consumes = { MediaType.APPLICATION_JSON_VALUE, "application/x-yaml" },
        produces = "application/json; charset=UTF-8"
    )
    public ResponseEntity<Project> updateProject(
        @RequestBody Project projectDTO,
        @ValidateField @PathVariable(name = "name", required = true) String name
    ) {
        return ResponseEntity.ok(this.projectService.updateProject(projectDTO, name));
    }

    @Operation(summary = "Delete a project", description = "Delete a specific project")
    @DeleteMapping(path = "/{name}")
    public ResponseEntity<Boolean> deleteProject(
        @ValidateField @PathVariable(name = "name", required = true) String name,
        @RequestParam(name = "cascade") Boolean cascade
    ) {
        return ResponseEntity.ok(this.projectService.deleteProject(name, cascade));
    }

    @Operation(summary = "List project functions", description = "Get all project function list")
    @GetMapping(path = "/{name}/functions", produces = "application/json; charset=UTF-8")
    public ResponseEntity<List<Function>> projectFunctions(@ValidateField @PathVariable String name) {
        return ResponseEntity.ok(this.projectService.getProjectFunctions(name));
    }

    @Operation(summary = "List project workflows", description = "Get all project workflow list")
    @GetMapping(path = "/{name}/workflows", produces = "application/json; charset=UTF-8")
    public ResponseEntity<List<Workflow>> projectWorkflows(@ValidateField @PathVariable String name) {
        return ResponseEntity.ok(this.projectService.getProjectWorkflows(name));
    }

    @Operation(summary = "List project artifacts", description = "Get all project artifact list")
    @GetMapping(path = "/{name}/artifacts", produces = "application/json; charset=UTF-8")
    public ResponseEntity<List<Artifact>> projectArtifacts(@ValidateField @PathVariable String name) {
        return ResponseEntity.ok(this.projectService.getProjectArtifacts(name));
    }

    @Operation(summary = "List project secrets", description = "Get all project secrets list")
    @GetMapping(path = "/{name}/secrets", produces = "application/json; charset=UTF-8")
    public ResponseEntity<List<Secret>> projectSecrets(@ValidateField @PathVariable String name) {
        return ResponseEntity.ok(this.projectService.getProjectSecrets(name));
    }

    @Operation(summary = "Read project secret data", description = "Get project secrets data for the specified keys")
    @GetMapping(path = "/{name}/secrets/data", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Map<String, String>> projectSecretData(
        @ValidateField @PathVariable String name,
        @RequestParam Set<String> keys
    ) {
        return ResponseEntity.ok(this.projectService.getProjectSecretData(name, keys));
    }

    @Operation(summary = "Store project secret data", description = "Store project secrets data")
    @PutMapping(path = "/{name}/secrets/data", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Void> storeProjectSecretData(
        @ValidateField @PathVariable String name,
        @RequestBody Map<String, String> values
    ) {
        this.projectService.storeProjectSecretData(name, values);
        return ResponseEntity.ok().build();
    }
}
