package it.smartcommunitylabdhub.core.controllers.v1.base;

import io.swagger.v3.oas.annotations.Operation;
import it.smartcommunitylabdhub.core.annotations.common.ApiVersion;
import it.smartcommunitylabdhub.core.annotations.validators.ValidateField;
import it.smartcommunitylabdhub.core.models.entities.artifact.Artifact;
import it.smartcommunitylabdhub.core.models.entities.function.Function;
import it.smartcommunitylabdhub.core.models.entities.project.Project;
import it.smartcommunitylabdhub.core.models.entities.workflow.Workflow;
import it.smartcommunitylabdhub.core.services.interfaces.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
@ApiVersion("v1")
@Validated
public class ProjectController {

    @Autowired
    ProjectService projectService;

    @Operation(summary = "List project", description = "Return a list of all projects")
    @GetMapping(path = "", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Page<Project>> getProjects(Pageable pageable) {
        return ResponseEntity.ok(this.projectService.getProjects(pageable));
    }

    @Operation(summary = "Create project", description = "Create an project and return")
    @PostMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE,
            "application/x-yaml"}, produces = "application/json; charset=UTF-8")
    public ResponseEntity<Project> createProject(@Valid @RequestBody Project projectDTO) {
        return ResponseEntity.ok(this.projectService.createProject(projectDTO));
    }

    @Operation(summary = "Get an project by uuid", description = "Return an project")
    @GetMapping(path = "/{uuidOrName}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Project> getProject(
            @ValidateField @PathVariable(name = "uuidOrName", required = true) String uuidOrName) {
        return ResponseEntity.ok(this.projectService.getProject(uuidOrName));
    }

    @Operation(summary = "Update specific project", description = "Update and return the project")
    @PutMapping(path = "/{uuidOrName}", consumes = {MediaType.APPLICATION_JSON_VALUE,
            "application/x-yaml"}, produces = "application/json; charset=UTF-8")
    public ResponseEntity<Project> updateProject(
            @RequestBody Project projectDTO,
            @ValidateField @PathVariable(name = "uuidOrName", required = true) String uuidOrName) {
        return ResponseEntity.ok(this.projectService.updateProject(projectDTO, uuidOrName));
    }

    @Operation(summary = "Delete a project", description = "Delete a specific project")
    @DeleteMapping(path = "/{uuidOrName}")
    public ResponseEntity<Boolean> deleteProject(
            @ValidateField @PathVariable(name = "uuidOrName", required = true) String uuidOrName) {
        return ResponseEntity.ok(this.projectService.deleteProject(uuidOrName));
    }

    @Operation(summary = "List project functions", description = "Get all project function list")
    @GetMapping(path = "/{uuidOrName}/functions", produces = "application/json; charset=UTF-8")
    public ResponseEntity<List<Function>> projectFunctions(
            @ValidateField @PathVariable String uuidOrName) {
        return ResponseEntity.ok(this.projectService.getProjectFunctions(uuidOrName));
    }

    @Operation(summary = "List project workflows", description = "Get all project workflow list")
    @GetMapping(path = "/{uuidOrName}/workflows", produces = "application/json; charset=UTF-8")
    public ResponseEntity<List<Workflow>> projectWorkflows(
            @ValidateField @PathVariable String uuidOrName) {
        return ResponseEntity.ok(this.projectService.getProjectWorkflows(uuidOrName));
    }

    @Operation(summary = "List project artifacts", description = "Get all project artifact list")
    @GetMapping(path = "/{uuidOrName}/artifacts", produces = "application/json; charset=UTF-8")
    public ResponseEntity<List<Artifact>> projectArtifacts(
            @ValidateField @PathVariable String uuidOrName) {
        return ResponseEntity.ok(this.projectService.getProjectArtifacts(uuidOrName));
    }

}
