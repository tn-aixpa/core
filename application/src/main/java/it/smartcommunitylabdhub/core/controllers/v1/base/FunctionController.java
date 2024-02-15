package it.smartcommunitylabdhub.core.controllers.v1.base;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.smartcommunitylabdhub.commons.annotations.validators.ValidateField;
import it.smartcommunitylabdhub.commons.models.entities.function.Function;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.services.FunctionService;
import it.smartcommunitylabdhub.core.annotations.ApiVersion;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
@RequestMapping("/functions")
@ApiVersion("v1")
@Validated
@Tag(name = "Function base API", description = "Endpoints related to functions management out of the Context")
public class FunctionController {

    @Autowired
    FunctionService functionService;

    @Operation(summary = "List functions", description = "Return a list of all functions")
    @GetMapping(path = "", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Page<Function>> getFunctions(@RequestParam Map<String, String> filter, Pageable pageable) {
        return ResponseEntity.ok(this.functionService.getFunctions(filter, pageable));
    }

    @Operation(summary = "Create function", description = "Create an function and return")
    @PostMapping(
        value = "",
        consumes = { MediaType.APPLICATION_JSON_VALUE, "application/x-yaml" },
        produces = "application/json; charset=UTF-8"
    )
    public ResponseEntity<Function> createFunction(@Valid @RequestBody Function functionDTO) {
        return ResponseEntity.ok(this.functionService.createFunction(functionDTO));
    }

    @Operation(summary = "Get a function by uuid", description = "Return an function")
    @GetMapping(path = "/{uuid}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Function> getFunction(
        @ValidateField @PathVariable(name = "uuid", required = true) String uuid
    ) {
        return ResponseEntity.ok(this.functionService.getFunction(uuid));
    }

    @Operation(summary = "Update specific function", description = "Update and return the function")
    @PutMapping(
        path = "/{uuid}",
        consumes = { MediaType.APPLICATION_JSON_VALUE, "application/x-yaml" },
        produces = "application/json; charset=UTF-8"
    )
    public ResponseEntity<Function> updateFunction(
        @Valid @RequestBody Function functionDTO,
        @ValidateField @PathVariable String uuid
    ) {
        return ResponseEntity.ok(this.functionService.updateFunction(functionDTO, uuid));
    }

    @Operation(summary = "Delete a function", description = "Delete a specific function")
    @DeleteMapping(path = "/{uuid}")
    public ResponseEntity<Boolean> deleteFunction(
        @ValidateField @PathVariable String uuid,
        @RequestParam(value = "cascade", defaultValue = "false") Boolean cascade
    ) {
        return ResponseEntity.ok(this.functionService.deleteFunction(uuid, cascade));
    }

    @Operation(summary = "Get function runs", description = "Given a function return the run list")
    @GetMapping(path = "/{uuid}/runs", produces = "application/json; charset=UTF-8")
    public ResponseEntity<List<Run>> functionRuns(@ValidateField @PathVariable String uuid) {
        return ResponseEntity.ok(this.functionService.getFunctionRuns(uuid));
    }
}
