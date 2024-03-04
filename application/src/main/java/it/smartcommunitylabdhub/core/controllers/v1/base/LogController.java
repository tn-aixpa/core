package it.smartcommunitylabdhub.core.controllers.v1.base;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.smartcommunitylabdhub.commons.Keys;
import it.smartcommunitylabdhub.commons.models.entities.log.Log;
import it.smartcommunitylabdhub.commons.services.LogService;
import it.smartcommunitylabdhub.core.annotations.ApiVersion;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logs")
@ApiVersion("v1")
@Tag(name = "Log base API", description = "Endpoints related to logs management out of the Context")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class LogController {

    @Autowired
    LogService logService;

    @Operation(summary = "Get specific log", description = "Given a uuid return a specific log")
    @GetMapping(path = "/{uuid}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Log> getLog(@Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String id) {
        return ResponseEntity.ok(this.logService.getLog(id));
    }

    @Operation(summary = "Log list", description = "Return the log list")
    @GetMapping(path = "", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Page<Log>> getLogs(Pageable pageable) {
        return ResponseEntity.ok(this.logService.getLogs(pageable));
    }

    @Operation(summary = "Delete a log", description = "Delete a specific log")
    @DeleteMapping(path = "/{uuid}")
    public ResponseEntity<Boolean> deleteLog(@Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String id) {
        return ResponseEntity.ok(this.logService.deleteLog(id));
    }
}
