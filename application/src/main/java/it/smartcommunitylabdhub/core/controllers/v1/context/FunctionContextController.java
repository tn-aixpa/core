package it.smartcommunitylabdhub.core.controllers.v1.context;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.smartcommunitylabdhub.commons.annotations.validators.ValidateField;
import it.smartcommunitylabdhub.commons.models.entities.function.Function;
import it.smartcommunitylabdhub.commons.models.entities.task.Task;
import it.smartcommunitylabdhub.commons.models.utils.TaskUtils;
import it.smartcommunitylabdhub.commons.services.RunService;
import it.smartcommunitylabdhub.commons.services.TaskService;
import it.smartcommunitylabdhub.core.annotations.ApiVersion;
import it.smartcommunitylabdhub.core.services.context.interfaces.FunctionContextService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@ApiVersion("v1")
@Validated
@Tag(name = "Function context API", description = "Endpoints related to functions management in Context")
public class FunctionContextController extends AbstractContextController {

    @Autowired
    FunctionContextService functionContextService;

    @Autowired
    TaskService taskService;

    @Autowired
    RunService runService;

    @Operation(
        summary = "Create an function in a project context",
        description = "First check if project exist and then create the function for the project (context)"
    )
    @PostMapping(
        value = "/functions",
        consumes = { MediaType.APPLICATION_JSON_VALUE, "application/x-yaml" },
        produces = "application/json; charset=UTF-8"
    )
    public ResponseEntity<Function> createFunction(
        @ValidateField @PathVariable String project,
        @Valid @RequestBody Function functionDTO
    ) {
        return ResponseEntity.ok(this.functionContextService.createFunction(project, functionDTO));
    }

    @Operation(
        summary = "Retrive only the latest version of all function",
        description = "First check if project exist and then return a list of the latest version of each function related to a project)"
    )
    @GetMapping(path = "/functions", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Page<Function>> getLatestFunctions(
        @RequestParam Map<String, String> filter,
        @ValidateField @PathVariable String project,
        Pageable pageable
    ) {
        return ResponseEntity.ok(this.functionContextService.getLatestByProjectName(filter, project, pageable));
    }

    @Operation(
        summary = "Retrieve all versions of the function sort by creation",
        description = "First check if project exist and then return a list of all version of the function sort by creation)"
    )
    @GetMapping(path = "/functions/{name}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Page<Function>> getAllFunctions(
        @RequestParam Map<String, String> filter,
        @ValidateField @PathVariable String project,
        @ValidateField @PathVariable String name,
        Pageable pageable
    ) {
        return ResponseEntity.ok(
            this.functionContextService.getByProjectNameAndFunctionName(filter, project, name, pageable)
        );
    }

    @Operation(
        summary = "Retrive a specific function version given the function uuid",
        description = "First check if project exist and then return a specific version of the function identified by the uuid)"
    )
    @GetMapping(path = "/functions/{name}/{uuid}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Function> getFunctionByUuid(
        @ValidateField @PathVariable String project,
        @ValidateField @PathVariable String name,
        @ValidateField @PathVariable String uuid
    ) {
        return ResponseEntity.ok(this.functionContextService.getByProjectAndFunctionAndUuid(project, name, uuid));
    }

    @Operation(
        summary = "Retrive the latest version of an function",
        description = "First check if project exist and then return the latest version of an function)"
    )
    @GetMapping(path = "/functions/{name}/latest", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Function> getLatestFunctionByName(
        @ValidateField @PathVariable String project,
        @ValidateField @PathVariable String name
    ) {
        return ResponseEntity.ok(this.functionContextService.getLatestByProjectNameAndFunctionName(project, name));
    }

    @Operation(
        summary = "Create an  or update an function in a project context",
        description = "First check if project exist, if function exist update one otherwise create a new version of the function"
    )
    @PostMapping(
        value = "/functions/{name}",
        consumes = { MediaType.APPLICATION_JSON_VALUE, "application/x-yaml" },
        produces = "application/json; charset=UTF-8"
    )
    public ResponseEntity<Function> createOrUpdateFunction(
        @ValidateField @PathVariable String project,
        @ValidateField @PathVariable String name,
        @Valid @RequestBody Function functionDTO
    ) {
        return ResponseEntity.ok(this.functionContextService.createOrUpdateFunction(project, name, functionDTO));
    }

    @Operation(
        summary = "Update if exist an function in a project context",
        description = "First check if project exist, if function exist update."
    )
    @PutMapping(
        value = "/functions/{name}/{uuid}",
        consumes = { MediaType.APPLICATION_JSON_VALUE, "application/x-yaml" },
        produces = "application/json; charset=UTF-8"
    )
    public ResponseEntity<Function> updateUpdateFunction(
        @ValidateField @PathVariable String project,
        @ValidateField @PathVariable String name,
        @ValidateField @PathVariable String uuid,
        @Valid @RequestBody Function functionDTO
    ) {
        return ResponseEntity.ok(this.functionContextService.updateFunction(project, name, uuid, functionDTO));
    }

    @Operation(
        summary = "Delete a specific function version",
        description = "First check if project exist, then delete a specific function version"
    )
    @DeleteMapping(path = "/functions/{name}/{uuid}")
    public ResponseEntity<Boolean> deleteSpecificFunctionVersion(
        @ValidateField @PathVariable String project,
        @ValidateField @PathVariable String name,
        @ValidateField @PathVariable String uuid
    ) {
        // Get function
        Function function = this.functionContextService.getByUuid(project, uuid);

        // Remove Task
        List<Task> taskList = this.taskService.getTasksByFunction(TaskUtils.buildTaskString(function));
        //Delete all related object
        taskList.forEach(task -> {
            // remove run
            this.runService.deleteRunsByTaskId(task.getId());

            // remove task
            this.taskService.deleteTask(task.getId(), false);
        });

        // Remove function and return
        return ResponseEntity.ok(this.functionContextService.deleteSpecificFunctionVersion(project, name, uuid));
    }

    @Operation(
        summary = "Delete all version of an function",
        description = "First check if project exist, then delete a specific function version"
    )
    @DeleteMapping(path = "/functions/{name}")
    public ResponseEntity<Boolean> deleteFunction(
        @ValidateField @PathVariable String project,
        @ValidateField @PathVariable String name
    ) {
        this.functionContextService.listByProjectNameAndFunctionName(project, name)
            .forEach(function -> {
                // Remove Task
                List<Task> taskList = this.taskService.getTasksByFunction(TaskUtils.buildTaskString(function));
                //Delete all related object
                taskList.forEach(task -> {
                    // remove run
                    this.runService.deleteRunsByTaskId(task.getId());

                    // remove task
                    this.taskService.deleteTask(task.getId(), false);
                });
            });

        return ResponseEntity.ok(this.functionContextService.deleteAllFunctionVersions(project, name));
    }
}
