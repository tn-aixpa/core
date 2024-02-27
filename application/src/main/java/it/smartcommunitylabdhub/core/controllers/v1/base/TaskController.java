package it.smartcommunitylabdhub.core.controllers.v1.base;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.smartcommunitylabdhub.commons.annotations.validators.ValidateField;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.models.entities.task.Task;
import it.smartcommunitylabdhub.commons.services.TaskService;
import it.smartcommunitylabdhub.core.annotations.ApiVersion;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/tasks")
@ApiVersion("v1")
@Tag(name = "Task base API", description = "Endpoints related to tasks management out of the Context")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class TaskController {

    @Autowired
    TaskService taskService;

    @Operation(summary = "Get specific task", description = "Given a uuid return a specific task")
    @GetMapping(path = "/{uuid}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Task> getTask(@ValidateField @PathVariable(name = "uuid", required = true) String uuid)
        throws NoSuchEntityException {
        return ResponseEntity.ok(this.taskService.getTask(uuid));
    }

    @Operation(summary = "List of tasks", description = "Return the list of all tasks")
    @GetMapping(path = "", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Page<Task>> getTasks(@RequestParam Map<String, String> filter, Pageable pageable) {
        return ResponseEntity.ok(this.taskService.getTasks(filter, pageable));
    }

    @Operation(summary = "Create a task", description = "Create and return a new task")
    @PostMapping(
        path = "",
        consumes = { MediaType.APPLICATION_JSON_VALUE, "application/x-yaml" },
        produces = "application/json; charset=UTF-8"
    )
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task taskDTO) {
        return ResponseEntity.ok(this.taskService.createTask(taskDTO));
    }

    @Operation(summary = "Update a task", description = "Update and return a task")
    @PutMapping(
        path = "/{uuid}",
        consumes = { MediaType.APPLICATION_JSON_VALUE, "application/x-yaml" },
        produces = "application/json; charset=UTF-8"
    )
    public ResponseEntity<Task> updateTask(
        @Valid @RequestBody Task functionDTO,
        @ValidateField @PathVariable String uuid
    ) throws NoSuchEntityException {
        return ResponseEntity.ok(this.taskService.updateTask(uuid, functionDTO));
    }

    @Operation(summary = "Delete a task", description = "Delete a specific task")
    @DeleteMapping(path = "/{uuid}")
    public ResponseEntity<Boolean> deleteTask(
        @ValidateField @PathVariable(name = "uuid", required = true) String uuid,
        @RequestParam(name = "cascade", defaultValue = "false") Boolean cascade
    ) {
        return ResponseEntity.ok(this.taskService.deleteTask(uuid, cascade));
    }
}
