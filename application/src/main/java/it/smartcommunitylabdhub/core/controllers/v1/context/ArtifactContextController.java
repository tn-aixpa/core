package it.smartcommunitylabdhub.core.controllers.v1.context;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.smartcommunitylabdhub.commons.Keys;
import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.exceptions.SystemException;
import it.smartcommunitylabdhub.commons.models.entities.artifact.Artifact;
import it.smartcommunitylabdhub.commons.models.queries.SearchFilter;
import it.smartcommunitylabdhub.core.ApplicationKeys;
import it.smartcommunitylabdhub.core.annotations.ApiVersion;
import it.smartcommunitylabdhub.core.models.entities.ArtifactEntity;
import it.smartcommunitylabdhub.core.models.files.ArtifactFilesService;
import it.smartcommunitylabdhub.core.models.queries.filters.entities.ArtifactEntityFilter;
import it.smartcommunitylabdhub.core.models.queries.services.SearchableArtifactService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.io.Serializable;
import java.util.Map;

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
import org.springframework.validation.BindException;
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
@RequestMapping("/-/{project}/artifacts")
@PreAuthorize(
    "hasAuthority('ROLE_ADMIN') or (hasAuthority(#project+':ROLE_USER') or hasAuthority(#project+':ROLE_ADMIN'))"
)
@Validated
@Slf4j
@Tag(name = "Artifact context API", description = "Endpoints related to artifacts management in project")
public class ArtifactContextController {

    @Autowired
    SearchableArtifactService artifactService;

    @Autowired
    ArtifactFilesService filesService;

    @Operation(summary = "Create an artifact in a project context")
    @PostMapping(
        value = "",
        consumes = { MediaType.APPLICATION_JSON_VALUE, "application/x-yaml" },
        produces = "application/json; charset=UTF-8"
    )
    public Artifact createArtifact(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @Valid @NotNull @RequestBody Artifact dto
    ) throws DuplicatedEntityException, IllegalArgumentException, SystemException, BindException {
        //enforce project match
        dto.setProject(project);

        //create as new
        return artifactService.createArtifact(dto);
    }

    @Operation(summary = "Search artifacts")
    @GetMapping(path = "", produces = "application/json; charset=UTF-8")
    public Page<Artifact> searchArtifacts(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @ParameterObject @Valid @Nullable ArtifactEntityFilter filter,
        @ParameterObject @RequestParam(required = false, defaultValue = "latest") String versions,
        @ParameterObject @PageableDefault(page = 0, size = ApplicationKeys.DEFAULT_PAGE_SIZE) @SortDefault.SortDefaults(
            { @SortDefault(sort = "created", direction = Direction.DESC) }
        ) Pageable pageable
    ) {
        SearchFilter<ArtifactEntity> sf = null;
        if (filter != null) {
            sf = filter.toSearchFilter();
        }
        if ("all".equals(versions)) {
            return artifactService.searchArtifactsByProject(project, pageable, sf);
        } else {
            return artifactService.searchLatestArtifactsByProject(project, pageable, sf);
        }
    }

    @Operation(summary = "Delete all version of an artifact")
    @DeleteMapping(path = "")
    public void deleteAllArtifacts(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @ParameterObject @RequestParam @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String name
    ) {
        artifactService.deleteArtifacts(project, name);
    }

    /*
     * Versions
     */

    @Operation(summary = "Retrieve a specific artifact version given the artifact id")
    @GetMapping(path = "/{id}", produces = "application/json; charset=UTF-8")
    public Artifact getArtifactById(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String id
    ) throws NoSuchEntityException {
        Artifact artifact = artifactService.getArtifact(id);

        //check for project and name match
        if (!artifact.getProject().equals(project)) {
            throw new IllegalArgumentException("invalid project");
        }

        return artifact;
    }

    @Operation(summary = "Update if exist an artifact in a project context")
    @PutMapping(
        value = "/{id}",
        consumes = { MediaType.APPLICATION_JSON_VALUE, "application/x-yaml" },
        produces = "application/json; charset=UTF-8"
    )
    public Artifact updateArtifactById(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String id,
        @RequestBody @Valid @NotNull Artifact artifactDTO
    ) throws NoSuchEntityException, IllegalArgumentException, SystemException, BindException {
        Artifact artifact = artifactService.getArtifact(id);

        //check for project and name match
        if (!artifact.getProject().equals(project)) {
            throw new IllegalArgumentException("invalid project");
        }

        return artifactService.updateArtifact(id, artifactDTO);
    }

    @Operation(summary = "Delete a specific artifact version")
    @DeleteMapping(path = "/{id}")
    public void deleteArtifactById(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String id
    ) throws NoSuchEntityException {
        Artifact artifact = artifactService.getArtifact(id);

        //check for project and name match
        if (!artifact.getProject().equals(project)) {
            throw new IllegalArgumentException("invalid project");
        }

        artifactService.deleteArtifact(id);
    }

    /*
     * Files
     */
    @Operation(summary = "Get download url for a given artifact, if available")
    @GetMapping(path = "/{id}/download", produces = "application/json; charset=UTF-8")
    public String downloadAsUrlArtifactById(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String id
    ) throws NoSuchEntityException {
        Artifact artifact = artifactService.getArtifact(id);

        //check for project and name match
        if (!artifact.getProject().equals(project)) {
            throw new IllegalArgumentException("invalid project");
        }

        return filesService.downloadArtifactAsUrl(id);
    }
    
    @Operation(summary = "Get object storage metadata for a given artifact, if available")
    @GetMapping(path = "/{id}/storage/metadata", produces = "application/json; charset=UTF-8")
    public Map<String, Serializable> getArtifactMetadataById(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String id
    ) throws NoSuchEntityException {
        Artifact artifact = artifactService.getArtifact(id);

        //check for project and name match
        if (!artifact.getProject().equals(project)) {
            throw new IllegalArgumentException("invalid project");
        }

        return filesService.getObjectMetadata(id);
    }
    
}
