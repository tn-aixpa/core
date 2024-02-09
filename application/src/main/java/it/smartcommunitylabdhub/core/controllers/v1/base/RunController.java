package it.smartcommunitylabdhub.core.controllers.v1.base;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.smartcommunitylabdhub.commons.annotations.validators.ValidateField;
import it.smartcommunitylabdhub.commons.infrastructure.factories.runnables.Runnable;
import it.smartcommunitylabdhub.commons.models.entities.log.Log;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.services.interfaces.LogService;
import it.smartcommunitylabdhub.commons.services.interfaces.RunService;
import it.smartcommunitylabdhub.commons.services.interfaces.RunnableStoreService;
import it.smartcommunitylabdhub.core.annotations.common.ApiVersion;
import it.smartcommunitylabdhub.fsm.pollers.PollingService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/runs")
@ApiVersion("v1")
@Tag(name = "Run base API", description = "Endpoints related to runs management out of the Context")
public class RunController {

    @Autowired
    RunService runService;

    @Autowired
    LogService logService;

    @Autowired
    //TODO remove
    PollingService pollingService;

    @Autowired
    RunnableStoreService<Runnable> runnableStoreService;

    @Operation(summary = "Get a run", description = "Given an uuid return the related Run")
    @GetMapping(path = "/{uuid}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Run> getRun(@ValidateField @PathVariable(name = "uuid", required = true) String uuid) {
        return ResponseEntity.ok(this.runService.getRun(uuid));
    }

    @Operation(summary = "Run log list", description = "Return the log list for a specific run")
    @GetMapping(path = "/{uuid}/log", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Page<Log>> getRunLog(
        @ValidateField @PathVariable(name = "uuid", required = true) String uuid,
        Pageable pageable
    ) {
        return ResponseEntity.ok(this.logService.getLogsByRunUuid(uuid, pageable));
    }

    @Operation(summary = "Run list", description = "Return a list of all runs")
    @GetMapping(path = "", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Page<Run>> getRuns(@RequestParam Map<String, String> filter, Pageable pageable) {
        return ResponseEntity.ok(this.runService.getRuns(filter, pageable));
    }

    @Operation(summary = "Create and execute a run", description = "Create a run and then execute it")
    @PostMapping(
        path = "",
        consumes = { MediaType.APPLICATION_JSON_VALUE, "application/x-yaml" },
        produces = "application/json; charset=UTF-8"
    )
    public ResponseEntity<Run> createRun(@Valid @RequestBody Run inputRunDTO) {
        return ResponseEntity.ok(this.runService.createRun(inputRunDTO));
    }

    @Operation(summary = "Update specific run", description = "Update and return the update run")
    @PutMapping(
        path = "/{uuid}",
        consumes = { MediaType.APPLICATION_JSON_VALUE, "application/x-yaml" },
        produces = "application/json; charset=UTF-8"
    )
    public ResponseEntity<Run> updateRun(@Valid @RequestBody Run runDTO, @ValidateField @PathVariable String uuid) {
        return ResponseEntity.ok(this.runService.updateRun(runDTO, uuid));
    }

    @Operation(summary = "Delete a run", description = "Delete a specific run")
    @DeleteMapping(path = "/{uuid}")
    public ResponseEntity<Boolean> deleteRun(
        @ValidateField @PathVariable(name = "uuid", required = true) String uuid,
        @RequestParam(name = "cascade", defaultValue = "false") Boolean cascade
    ) {
        return ResponseEntity.ok(this.runService.deleteRun(uuid, cascade));
    }

    @Operation(summary = "Stop a run", description = "Stop a specific run")
    @PostMapping(
        path = "/{uuid}/stop",
        consumes = { MediaType.APPLICATION_JSON_VALUE, "application/x-yaml" },
        produces = "application/json; charset=UTF-8"
    )
    public ResponseEntity<Boolean> stopRun(@ValidateField @PathVariable String uuid) {
        Runnable runnable = runnableStoreService.find(uuid);
        //TODO refactor! the framework is responsible for managing runs, not the controller
        pollingService.stopOne(runnable.getId());

        // Do other operation to stop poller.
        return ResponseEntity.ok(true);
    }
}
