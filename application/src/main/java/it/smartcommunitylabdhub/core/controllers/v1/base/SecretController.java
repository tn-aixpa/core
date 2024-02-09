package it.smartcommunitylabdhub.core.controllers.v1.base;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.smartcommunitylabdhub.commons.annotations.validators.ValidateField;
import it.smartcommunitylabdhub.commons.models.entities.secret.Secret;
import it.smartcommunitylabdhub.commons.services.interfaces.ProjectSecretService;
import it.smartcommunitylabdhub.core.annotations.common.ApiVersion;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/secrets")
@ApiVersion("v1")
@Tag(name = "Secret base API", description = "Endpoints related to secret management out of the Context")
public class SecretController {

    @Autowired
    ProjectSecretService secretService;

    @Operation(summary = "Get specific secret", description = "Given a uuid return a specific secret")
    @GetMapping(path = "/{uuid}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Secret> getSecret(@ValidateField @PathVariable(name = "uuid", required = true) String uuid) {
        return ResponseEntity.ok(this.secretService.getProjectSecret(uuid));
    }

    @Operation(summary = "Create a secret", description = "Create and return a new secret")
    @PostMapping(
        path = "",
        consumes = { MediaType.APPLICATION_JSON_VALUE, "application/x-yaml" },
        produces = "application/json; charset=UTF-8"
    )
    public ResponseEntity<Secret> createSecret(@Valid @RequestBody Secret secretDTO) {
        return ResponseEntity.ok(this.secretService.createProjectSecret(secretDTO));
    }

    @Operation(summary = "Update a secret", description = "Update and return a secret")
    @PutMapping(
        path = "/{uuid}",
        consumes = { MediaType.APPLICATION_JSON_VALUE, "application/x-yaml" },
        produces = "application/json; charset=UTF-8"
    )
    public ResponseEntity<Secret> updateSecret(
        @Valid @RequestBody Secret functionDTO,
        @ValidateField @PathVariable String uuid
    ) {
        return ResponseEntity.ok(this.secretService.updateProjectSecret(functionDTO, uuid));
    }

    @Operation(summary = "Delete a secret", description = "Delete a specific secret")
    @DeleteMapping(path = "/{uuid}")
    public ResponseEntity<Boolean> deleteSecret(
        @ValidateField @PathVariable(name = "uuid", required = true) String uuid
    ) {
        return ResponseEntity.ok(this.secretService.deleteProjectSecret(uuid));
    }
}
