package it.smartcommunitylabdhub.core.controllers.v1.base;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.smartcommunitylabdhub.commons.Keys;
import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.models.entities.artifact.Artifact;
import it.smartcommunitylabdhub.commons.models.queries.SearchFilter;
import it.smartcommunitylabdhub.core.ApplicationKeys;
import it.smartcommunitylabdhub.core.annotations.ApiVersion;
import it.smartcommunitylabdhub.core.models.entities.artifact.ArtifactEntity;
import it.smartcommunitylabdhub.core.models.queries.filters.entities.ArtifactEntityFilter;
import it.smartcommunitylabdhub.core.models.queries.services.SearchableArtifactService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import javax.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
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
@ApiVersion("v1")
@RequestMapping("/artifacts")
//TODO evaluate permissions for project via lookup in dto
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Validated
@Slf4j
@Tag(name = "Artifact base API", description = "Endpoints related to artifacts management out of the Context")
public class ArtifactController {

    @Autowired
    SearchableArtifactService artifactService;

    @Operation(summary = "Create artifact", description = "Create an artifact and return")
    @PostMapping(
        value = "",
        consumes = { MediaType.APPLICATION_JSON_VALUE, "application/x-yaml" },
        produces = "application/json; charset=UTF-8"
    )
    public Artifact createArtifact(@RequestBody @Valid @NotNull Artifact dto) throws DuplicatedEntityException {
        return artifactService.createArtifact(dto);
    }

    @Operation(summary = "List artifacts", description = "Return a list of all artifacts")
    @GetMapping(path = "", produces = "application/json; charset=UTF-8")
    public Page<Artifact> getArtifacts(
        @ParameterObject @RequestParam(required = false) @Valid @Nullable ArtifactEntityFilter filter,
        @ParameterObject @PageableDefault(page = 0, size = ApplicationKeys.DEFAULT_PAGE_SIZE) @SortDefault.SortDefaults(
            { @SortDefault(sort = "created", direction = Direction.DESC) }
        ) Pageable pageable
    ) {
        SearchFilter<ArtifactEntity> sf = null;
        if (filter != null) {
            sf = filter.toSearchFilter();
        }

        return artifactService.searchArtifacts(pageable, sf);
    }

    @Operation(summary = "Get an artifact by id", description = "Return an artifact")
    @GetMapping(path = "/{id}", produces = "application/json; charset=UTF-8")
    public Artifact getArtifact(@PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String id)
        throws NoSuchEntityException {
        return artifactService.getArtifact(id);
    }

    @Operation(summary = "Update specific artifact", description = "Update and return the artifact")
    @PutMapping(
        path = "/{id}",
        consumes = { MediaType.APPLICATION_JSON_VALUE, "application/x-yaml" },
        produces = "application/json; charset=UTF-8"
    )
    public Artifact updateArtifact(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String id,
        @RequestBody @Valid @NotNull Artifact dto
    ) throws NoSuchEntityException {
        return artifactService.updateArtifact(id, dto);
    }

    @Operation(summary = "Delete an artifact", description = "Delete a specific artifact")
    @DeleteMapping(path = "/{id}")
    public void deleteArtifact(@PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String id) {
        artifactService.deleteArtifact(id);
    }
}
