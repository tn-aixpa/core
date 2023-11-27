package it.smartcommunitylabdhub.core.controllers.v1.base;

import io.swagger.v3.oas.annotations.Operation;
import it.smartcommunitylabdhub.core.annotations.common.ApiVersion;
import it.smartcommunitylabdhub.core.annotations.validators.ValidateField;
import it.smartcommunitylabdhub.core.models.entities.artifact.Artifact;
import it.smartcommunitylabdhub.core.services.interfaces.ArtifactService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/artifacts")
@ApiVersion("v1")
@Validated
public class ArtifactController {

    @Autowired
    ArtifactService artifactService;

    @Operation(summary = "List artifacts", description = "Return a list of all artifacts")
    @GetMapping(path = "", produces = "application/json; charset=UTF-8")
    public ResponseEntity<List<Artifact>> getArtifacts(Pageable pageable) {
        return ResponseEntity.ok(this.artifactService.getArtifacts(pageable));
    }

    @Operation(summary = "Create artifact", description = "Create an artifact and return")
    @PostMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE,
            "application/x-yaml"}, produces = "application/json; charset=UTF-8")
    public ResponseEntity<Artifact> createArtifact(@Valid @RequestBody Artifact artifactDTO) {
        return ResponseEntity.ok(this.artifactService.createArtifact(artifactDTO));
    }

    @Operation(summary = "Get an artifact by uuid", description = "Return an artifact")
    @GetMapping(path = "/{uuid}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Artifact> getArtifact(
            @ValidateField @PathVariable(name = "uuid", required = true) String uuid) {
        return ResponseEntity.ok(this.artifactService.getArtifact(uuid));
    }

    @Operation(summary = "Update specific artifact", description = "Update and return the artifact")
    @PutMapping(path = "/{uuid}", consumes = {MediaType.APPLICATION_JSON_VALUE,
            "application/x-yaml"}, produces = "application/json; charset=UTF-8")
    public ResponseEntity<Artifact> updateArtifact(@Valid @RequestBody Artifact artifactDTO,
                                                   @ValidateField @PathVariable String uuid) {
        return ResponseEntity.ok(this.artifactService.updateArtifact(artifactDTO, uuid));
    }

    @Operation(summary = "Delete an artifact", description = "Delete a specific artifact")
    @DeleteMapping(path = "/{uuid}")
    public ResponseEntity<Boolean> deleteArtifact(@ValidateField @PathVariable String uuid) {
        return ResponseEntity.ok(this.artifactService.deleteArtifact(uuid));
    }

}
