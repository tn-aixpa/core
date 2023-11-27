package it.smartcommunitylabdhub.core.controllers.v1.base;

import io.swagger.v3.oas.annotations.Operation;
import it.smartcommunitylabdhub.core.annotations.common.ApiVersion;
import it.smartcommunitylabdhub.core.annotations.validators.ValidateField;
import it.smartcommunitylabdhub.core.models.entities.function.Function;
import it.smartcommunitylabdhub.core.models.entities.run.Run;
import it.smartcommunitylabdhub.core.services.interfaces.FunctionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/functions")
@ApiVersion("v1")
@Validated
public class FunctionController {

    @Autowired
    FunctionService functionService;

    @Operation(summary = "List functions", description = "Return a list of all functions")
    @GetMapping(path = "", produces = "application/json; charset=UTF-8")
    public ResponseEntity<List<Function>> getFunctions(Pageable pageable) {
        return ResponseEntity.ok(this.functionService.getFunctions(pageable));
    }

    @Operation(summary = "Create function", description = "Create an function and return")
    @PostMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE,
            "application/x-yaml"}, produces = "application/json; charset=UTF-8")
    public ResponseEntity<Function> createFunction(@Valid @RequestBody Function functionDTO) {
        return ResponseEntity.ok(this.functionService.createFunction(functionDTO));
    }

    @Operation(summary = "Get a function by uuid", description = "Return an function")
    @GetMapping(path = "/{uuid}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Function> getFunction(
            @ValidateField @PathVariable(name = "uuid", required = true) String uuid) {
        return ResponseEntity.ok(this.functionService.getFunction(uuid));
    }

    @Operation(summary = "Update specific function", description = "Update and return the function")
    @PutMapping(path = "/{uuid}", consumes = {MediaType.APPLICATION_JSON_VALUE,
            "application/x-yaml"}, produces = "application/json; charset=UTF-8")
    public ResponseEntity<Function> updateFunction(@Valid @RequestBody Function functionDTO,
                                                   @ValidateField @PathVariable String uuid) {
        return ResponseEntity.ok(this.functionService.updateFunction(functionDTO, uuid));
    }

    @Operation(summary = "Delete a function", description = "Delete a specific function")
    @DeleteMapping(path = "/{uuid}")
    public ResponseEntity<Boolean> deleteFunction(@ValidateField @PathVariable String uuid) {
        return ResponseEntity.ok(this.functionService.deleteFunction(uuid));
    }

    @Operation(summary = "Get function runs", description = "Given a function return the run list")
    @GetMapping(path = "/{uuid}/runs", produces = "application/json; charset=UTF-8")
    public ResponseEntity<List<Run>> functionRuns(@ValidateField @PathVariable String uuid) {
        return ResponseEntity.ok(this.functionService.getFunctionRuns(uuid));
    }
}
