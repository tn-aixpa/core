package it.smartcommunitylabdhub.core.controllers.v1.base;

import io.swagger.v3.oas.annotations.Operation;
import it.smartcommunitylabdhub.core.annotations.common.ApiVersion;
import it.smartcommunitylabdhub.core.annotations.validators.ValidateField;
import it.smartcommunitylabdhub.core.models.entities.run.Run;
import it.smartcommunitylabdhub.core.models.entities.workflow.Workflow;
import it.smartcommunitylabdhub.core.services.interfaces.WorkflowService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workflows")
@ApiVersion("v1")
@Validated
public class WorkflowController {

    @Autowired
    WorkflowService workflowService;

    @Operation(summary = "List workflows", description = "Return a list of all workflows")
    @GetMapping(path = "", produces = "application/json; charset=UTF-8")
    public ResponseEntity<List<Workflow>> getWorkflows(Pageable pageable) {
        return ResponseEntity.ok(this.workflowService.getWorkflows(pageable));
    }

    @Operation(summary = "Create workflow", description = "Create an workflow and return")
    @PostMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE,
            "application/x-yaml"}, produces = "application/json; charset=UTF-8")
    public ResponseEntity<Workflow> createWorkflow(@Valid @RequestBody Workflow workflowDTO) {
        return ResponseEntity.ok(this.workflowService.createWorkflow(workflowDTO));
    }

    @Operation(summary = "Get an workflow by uuid", description = "Return an workflow")
    @GetMapping(path = "/{uuid}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Workflow> getWorkflow(
            @ValidateField @PathVariable(name = "uuid", required = true) String uuid) {
        return ResponseEntity.ok(this.workflowService.getWorkflow(uuid));
    }

    @Operation(summary = "Update specific workflow", description = "Update and return the workflow")
    @PutMapping(path = "/{uuid}", consumes = {MediaType.APPLICATION_JSON_VALUE,
            "application/x-yaml"}, produces = "application/json; charset=UTF-8")
    public ResponseEntity<Workflow> updateWorkflow(@Valid @RequestBody Workflow workflowDTO,
                                                   @ValidateField @PathVariable String uuid) {
        return ResponseEntity.ok(this.workflowService.updateWorkflow(workflowDTO, uuid));
    }

    @Operation(summary = "Delete an workflow", description = "Delete a specific workflow")
    @DeleteMapping(path = "/{uuid}")
    public ResponseEntity<Boolean> deleteWorkflow(@ValidateField @PathVariable String uuid) {
        return ResponseEntity.ok(this.workflowService.deleteWorkflow(uuid));
    }

    @GetMapping(path = "/{uuid}/runs", produces = "application/json; charset=UTF-8")
    public ResponseEntity<List<Run>> workflowRuns(@ValidateField @PathVariable String uuid) {
        return ResponseEntity.ok(this.workflowService.getWorkflowRuns(uuid));
    }

}
