package it.smartcommunitylabdhub.core.controllers.v1.base;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.smartcommunitylabdhub.commons.annotations.validators.ValidateField;
import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/functions")
@ApiVersion("v1")
@Validated
@Tag(name = "Function base API", description = "Endpoints related to functions management out of the Context")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
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
    public ResponseEntity<Function> createFunction(@Valid @RequestBody Function functionDTO)
        throws DuplicatedEntityException {
        return ResponseEntity.ok(this.functionService.createFunction(functionDTO));
    }

    @Operation(summary = "Get a function by uuid", description = "Return an function")
    @GetMapping(path = "/{uuid}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Function> getFunction(
        @ValidateField @PathVariable(name = "uuid", required = true) String uuid
    ) throws NoSuchEntityException {
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
    ) throws NoSuchEntityException {
        return ResponseEntity.ok(this.functionService.updateFunction(uuid, functionDTO));
    }

    @Operation(summary = "Delete a function", description = "Delete a specific function")
    @DeleteMapping(path = "/{uuid}")
    public ResponseEntity<Void> deleteFunction(
        @ValidateField @PathVariable String uuid,
        @RequestParam(value = "cascade", defaultValue = "false") Boolean cascade
    ) {
        this.functionService.deleteFunction(uuid, cascade);
        return ResponseEntity.ok().build();
    }

    @Deprecated
    @Operation(summary = "Get function runs", description = "Given a function return the run list")
    @GetMapping(path = "/{uuid}/runs", produces = "application/json; charset=UTF-8")
    public ResponseEntity<List<Run>> functionRuns(@ValidateField @PathVariable String uuid)
        throws NoSuchEntityException {
        return ResponseEntity.ok(this.functionService.getFunctionRuns(uuid));
    }
}
