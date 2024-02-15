package it.smartcommunitylabdhub.core.controllers.v1.context;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.smartcommunitylabdhub.commons.annotations.validators.ValidateField;
import it.smartcommunitylabdhub.commons.models.entities.secret.Secret;
import it.smartcommunitylabdhub.commons.services.RunService;
import it.smartcommunitylabdhub.core.annotations.ApiVersion;
import it.smartcommunitylabdhub.core.services.context.interfaces.SecretContextService;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ApiVersion("v1")
@Validated
@Tag(name = "Secret context API", description = "Endpoints related to secrets management in Context")
public class SecretContextController extends AbstractContextController {

    @Autowired
    SecretContextService secretContextService;

    @Autowired
    RunService runService;

    @Operation(
        summary = "Create a secret in a project context",
        description = "First check if project exist and then create the secret for the project (context)"
    )
    @PostMapping(
        value = "/secrets",
        consumes = { MediaType.APPLICATION_JSON_VALUE, "application/x-yaml" },
        produces = "application/json; charset=UTF-8"
    )
    public ResponseEntity<Secret> createSecret(
        @ValidateField @PathVariable String project,
        @Valid @RequestBody Secret secretDTO
    ) {
        return ResponseEntity.ok(this.secretContextService.createSecret(project, secretDTO));
    }

    @Operation(
        summary = "Retrive only the latest version of all secret",
        description = "First check if project exist and then return a list of secrets related with the project)"
    )
    @GetMapping(path = "/secrets", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Page<Secret>> getLatestSecrets(
        @RequestParam Map<String, String> filter,
        @ValidateField @PathVariable String project,
        Pageable pageable
    ) {
        return ResponseEntity.ok(this.secretContextService.getAllSecretsByProjectName(filter, project, pageable));
    }

    @Operation(
        summary = "Retrieve a specific secret given the secret uuid",
        description = "First check if project exist and then return a specific version of the secret identified by the uuid)"
    )
    @GetMapping(path = "/secrets/{uuid}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Secret> getSecretByUuid(
        @ValidateField @PathVariable String project,
        @ValidateField @PathVariable String uuid
    ) {
        return ResponseEntity.ok(this.secretContextService.getByProjectAndSecretUuid(project, uuid));
    }

    @Operation(
        summary = "Update if exist a secret in a project context",
        description = "First check if project exist, if secret exist update."
    )
    @PutMapping(
        value = "/secrets/{uuid}",
        consumes = { MediaType.APPLICATION_JSON_VALUE, "application/x-yaml" },
        produces = "application/json; charset=UTF-8"
    )
    public ResponseEntity<Secret> updateUpdateSecret(
        @ValidateField @PathVariable String project,
        @ValidateField @PathVariable String uuid,
        @Valid @RequestBody Secret secretDTO
    ) {
        return ResponseEntity.ok(this.secretContextService.updateSecret(project, uuid, secretDTO));
    }

    @Operation(
        summary = "Delete a specific secret version",
        description = "First check if project exist, then delete a specific secret version"
    )
    @DeleteMapping(path = "/secrets/{uuid}")
    public ResponseEntity<Boolean> deleteSpecificSecretVersion(
        @ValidateField @PathVariable String project,
        @ValidateField @PathVariable String uuid
    ) {
        // Remove secret and return
        return ResponseEntity.ok(this.secretContextService.deleteSpecificSecretVersion(project, uuid));
    }
}
