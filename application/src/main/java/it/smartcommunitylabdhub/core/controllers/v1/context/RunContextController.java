package it.smartcommunitylabdhub.core.controllers.v1.context;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.smartcommunitylabdhub.commons.annotations.validators.ValidateField;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.services.interfaces.RunService;
import it.smartcommunitylabdhub.core.annotations.ApiVersion;
import it.smartcommunitylabdhub.core.services.context.interfaces.RunContextService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@ApiVersion("v1")
@Validated
@Tag(name = "Run context API", description = "Endpoints related to runs management in Context")
public class RunContextController extends AbstractContextController {

    @Autowired
    RunContextService RunContextService;
    

    @Autowired
    RunService runService;

    @Operation(
            summary = "Create a Run in a project context",
            description = "First check if project exist and then create the Run for the project (context)"
    )
    @PostMapping(
            value = "/runs",
            consumes = {MediaType.APPLICATION_JSON_VALUE, "application/x-yaml"},
            produces = "application/json; charset=UTF-8"
    )
    public ResponseEntity<Run> createRun(@ValidateField @PathVariable String project, @Valid @RequestBody Run RunDTO) {
        return ResponseEntity.ok(this.RunContextService.createRun(project, RunDTO));
    }

    @Operation(
            summary = "Retrive only the latest version of all Run",
            description = "First check if project exist and then return a list of Runs related with the project)"
    )
    @GetMapping(path = "/runs", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Page<Run>> getLatestRuns(
            @RequestParam Map<String, String> filter,
            @ValidateField @PathVariable String project,
            Pageable pageable
    ) {
        return ResponseEntity.ok(this.RunContextService.getAllRunsByProjectName(filter, project, pageable));
    }

    @Operation(
            summary = "Retrieve a specific Run given the Run uuid",
            description = "First check if project exist and then return a specific version of the Run identified by the uuid)"
    )
    @GetMapping(path = "/runs/{uuid}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Run> getRunByUuid(
            @ValidateField @PathVariable String project,
            @ValidateField @PathVariable String uuid
    ) {
        return ResponseEntity.ok(this.RunContextService.getByProjectAndRunUuid(project, uuid));
    }

    @Operation(
            summary = "Update if exist a Run in a project context",
            description = "First check if project exist, if Run exist update."
    )
    @PutMapping(
            value = "/runs/{uuid}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, "application/x-yaml"},
            produces = "application/json; charset=UTF-8"
    )
    public ResponseEntity<Run> updateUpdateRun(
            @ValidateField @PathVariable String project,
            @ValidateField @PathVariable String uuid,
            @Valid @RequestBody Run RunDTO
    ) {
        return ResponseEntity.ok(this.RunContextService.updateRun(project, uuid, RunDTO));
    }

    @Operation(
            summary = "Delete a specific Run version",
            description = "First check if project exist, then delete a specific Run version"
    )
    @DeleteMapping(path = "/runs/{uuid}")
    public ResponseEntity<Boolean> deleteSpecificRunVersion(
            @ValidateField @PathVariable String project,
            @ValidateField @PathVariable String uuid
    ) {
        // Remove Run and return
        return ResponseEntity.ok(this.RunContextService.deleteSpecificRunVersion(project, uuid));
    }
}
