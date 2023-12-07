package it.smartcommunitylabdhub.core.controllers.v1.context;

import io.swagger.v3.oas.annotations.Operation;
import it.smartcommunitylabdhub.core.annotations.common.ApiVersion;
import it.smartcommunitylabdhub.core.annotations.validators.ValidateField;
import it.smartcommunitylabdhub.core.models.entities.task.Task;
import it.smartcommunitylabdhub.core.services.context.interfaces.TaskContextService;
import it.smartcommunitylabdhub.core.services.interfaces.RunService;
import it.smartcommunitylabdhub.core.services.interfaces.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@ApiVersion("v1")
@Validated
public class TaskContextController implements ContextController {

    @Autowired
    TaskContextService taskContextService;

    @Autowired
    TaskService taskService;

    @Autowired
    RunService runService;

    @Operation(summary = "Create a task in a project context",
            description = "First check if project exist and then create the task for the project (context)")
    @PostMapping(value = "/tasks", consumes = {MediaType.APPLICATION_JSON_VALUE,
            "application/x-yaml"}, produces = "application/json; charset=UTF-8")
    public ResponseEntity<Task> createTask(
            @ValidateField @PathVariable String project,
            @Valid @RequestBody Task taskDTO) {
        return ResponseEntity.ok(
                this.taskContextService.createTask(project, taskDTO));
    }

    @Operation(summary = "Retrive only the latest version of all task",
            description = "First check if project exist and then return a list of tasks related with the project)")
    @GetMapping(path = "/tasks", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Page<Task>> getLatestTasks(
            @ValidateField @PathVariable String project,
            Pageable pageable) {

        return ResponseEntity.ok(this.taskContextService
                .getAllTasksByProjectName(project, pageable));
    }


    @Operation(summary = "Retrieve a specific task given the task uuid",
            description = "First check if project exist and then return a specific version of the task identified by the uuid)")
    @GetMapping(path = "/tasks/{uuid}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Task> getTaskByUuid(
            @ValidateField @PathVariable String project,
            @ValidateField @PathVariable String uuid) {

        return ResponseEntity.ok(this.taskContextService
                .getByProjectAndTaskUuid(project, uuid));

    }


    @Operation(summary = "Update if exist a task in a project context",
            description = "First check if project exist, if task exist update.")
    @PutMapping(value = "/tasks/{uuid}",
            consumes = {MediaType.APPLICATION_JSON_VALUE,
                    "application/x-yaml"},
            produces = "application/json; charset=UTF-8")
    public ResponseEntity<Task> updateUpdateTask(
            @ValidateField @PathVariable String project,
            @ValidateField @PathVariable String uuid,
            @Valid @RequestBody Task taskDTO) {
        return ResponseEntity
                .ok(this.taskContextService.updateTask(
                        project, uuid, taskDTO));
    }

    @Operation(summary = "Delete a specific task version",
            description = "First check if project exist, then delete a specific task version")
    @DeleteMapping(path = "/tasks/{uuid}")
    public ResponseEntity<Boolean> deleteSpecificTaskVersion(
            @ValidateField @PathVariable String project,
            @ValidateField @PathVariable String uuid) {


        // Remove task and return
        return ResponseEntity
                .ok(this.taskContextService.deleteSpecificTaskVersion(
                        project, uuid));
    }

}
