package it.smartcommunitylabdhub.core.controllers.v1.base;

import io.swagger.v3.oas.annotations.Operation;
import it.smartcommunitylabdhub.core.annotations.common.ApiVersion;
import it.smartcommunitylabdhub.core.annotations.validators.ValidateField;
import it.smartcommunitylabdhub.core.models.entities.log.LogDTO;
import it.smartcommunitylabdhub.core.models.entities.run.XRun;
import it.smartcommunitylabdhub.core.services.interfaces.LogService;
import it.smartcommunitylabdhub.core.services.interfaces.RunService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/runs")
@ApiVersion("v1")
public class RunController {

    @Autowired
    RunService runService;

    @Autowired
    LogService logService;

    @Operation(summary = "Get a run", description = "Given an uuid return the related Run")
    @GetMapping(path = "/{uuid}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<XRun> getRun(
            @ValidateField @PathVariable(name = "uuid", required = true) String uuid) {
        return ResponseEntity.ok(this.runService.getRun(uuid));
    }

    @Operation(summary = "Run log list", description = "Return the log list for a specific run")
    @GetMapping(path = "/{uuid}/log", produces = "application/json; charset=UTF-8")
    public ResponseEntity<List<LogDTO>> getRunLog(
            @ValidateField @PathVariable(name = "uuid", required = true) String uuid) {
        return ResponseEntity.ok(this.logService.getLogsByRunUuid(uuid));
    }

    @Operation(summary = "Run list", description = "Return a list of all runs")
    @GetMapping(path = "", produces = "application/json; charset=UTF-8")
    public ResponseEntity<List<XRun>> getRuns(Pageable pageable) {
        return ResponseEntity.ok(this.runService.getRuns(pageable));
    }

    @Operation(summary = "Create and execute a run",
            description = "Create a run and then execute it")
    @PostMapping(path = "", consumes = {MediaType.APPLICATION_JSON_VALUE,
            "application/x-yaml"}, produces = "application/json; charset=UTF-8")
    public ResponseEntity<XRun> createRun(@Valid @RequestBody XRun inputRunDTO) {
        return ResponseEntity.ok(this.runService.createRun(inputRunDTO));
    }


    @Operation(summary = "Update specific run", description = "Update and return the update run")
    @PutMapping(path = "/{uuid}", consumes = {MediaType.APPLICATION_JSON_VALUE,
            "application/x-yaml"}, produces = "application/json; charset=UTF-8")
    public ResponseEntity<XRun> updateRun(@Valid @RequestBody XRun runDTO,
                                          @ValidateField @PathVariable String uuid) {
        return ResponseEntity.ok(this.runService.updateRun(runDTO, uuid));
    }


    @Operation(summary = "Delete a run", description = "Delete a specific run")
    @DeleteMapping(path = "/{uuid}")
    public ResponseEntity<Boolean> deleteRun(
            @ValidateField @PathVariable(name = "uuid", required = true) String uuid) {
        return ResponseEntity.ok(this.runService.deleteRun(uuid));
    }
}
