package it.smartcommunitylabdhub.core.controllers.v1.context;

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

    @Operation(
        summary = "Create an artifact in a project context",
        description = "create the artifact for the project (context)"
    )
    @PostMapping(
        value = "",
        consumes = { MediaType.APPLICATION_JSON_VALUE, "application/x-yaml" },
        produces = "application/json; charset=UTF-8"
    )
    public Artifact createArtifact(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @Valid @NotNull @RequestBody Artifact dto
    ) throws DuplicatedEntityException {
        //enforce project match
        dto.setProject(project);

        String name = dto.getName();

        try {
            @SuppressWarnings("unused")
            Artifact artifact = artifactService.getLatestArtifact(project, name);

            //there is already an entity with the same name
            throw new DuplicatedEntityException(name);
        } catch (NoSuchEntityException e) {
            //create as new
            return artifactService.createArtifact(dto);
        }
    }

    @Operation(
        summary = "Retrieve only the latest version of all artifacts",
        description = "return a list of the latest version of each artifact related to a project"
    )
    @GetMapping(path = "", produces = "application/json; charset=UTF-8")
    public Page<Artifact> getLatestArtifacts(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @RequestParam(required = false) @Valid @Nullable ArtifactEntityFilter filter,
        @PageableDefault(page = 0, size = ApplicationKeys.DEFAULT_PAGE_SIZE) @SortDefault.SortDefaults(
            { @SortDefault(sort = "created", direction = Direction.DESC) }
        ) Pageable pageable
    ) {
        SearchFilter<ArtifactEntity> sf = null;
        if (filter != null) {
            sf = filter.toSearchFilter();
        }

        return artifactService.searchLatestArtifactsByProject(project, pageable, sf);
    }

    @Operation(
        summary = "Retrieve all versions of the artifact sort by creation",
        description = "return a list of all version of the artifact sort by creation"
    )
    @GetMapping(path = "/{name}", produces = "application/json; charset=UTF-8")
    public Page<Artifact> getAllArtifacts(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String name,
        @PageableDefault(page = 0, size = ApplicationKeys.DEFAULT_PAGE_SIZE) @SortDefault.SortDefaults(
            { @SortDefault(sort = "created", direction = Direction.DESC) }
        ) Pageable pageable
    ) {
        return artifactService.findArtifacts(project, name, pageable);
    }

    @Operation(
        summary = "Create a new version of an artifact in a project context",
        description = "if artifact exist create a new version of the artifact"
    )
    @PostMapping(
        value = "/{name}",
        consumes = { MediaType.APPLICATION_JSON_VALUE, "application/x-yaml" },
        produces = "application/json; charset=UTF-8"
    )
    public Artifact createOrUpdateArtifact(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String name,
        @RequestBody @Valid @NotNull Artifact dto
    ) throws NoSuchEntityException, DuplicatedEntityException {
        //enforce project match
        dto.setProject(project);
        dto.setName(name);

        @SuppressWarnings("unused")
        Artifact artifact = artifactService.getLatestArtifact(project, name);
        artifact = artifactService.createArtifact(dto);

        return artifact;
    }

    @Operation(
        summary = "Delete all version of an artifact",
        description = "First check if project exist, then delete a specific artifact version"
    )
    @DeleteMapping(path = "/{name}")
    public void deleteArtifact(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String name
    ) {
        artifactService.deleteArtifacts(project, name);
    }

    /*
     * Versions
     */

    @Operation(
        summary = "Retrieve the latest version of an artifact",
        description = "return the latest version of an artifact"
    )
    @GetMapping(path = "/{name}/latest", produces = "application/json; charset=UTF-8")
    public Artifact getLatestArtifactByName(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String name
    ) throws NoSuchEntityException {
        return artifactService.getLatestArtifact(project, name);
    }

    @Operation(
        summary = "Retrieve a specific artifact version given the artifact id",
        description = "return a specific version of the artifact identified by the id"
    )
    @GetMapping(path = "/{name}/{id}", produces = "application/json; charset=UTF-8")
    public Artifact getArtifactById(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String name,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String id
    ) throws NoSuchEntityException {
        Artifact artifact = artifactService.getArtifact(id);

        //check for project and name match
        if (!artifact.getProject().equals(project) || !artifact.getName().equals(name)) {
            throw new IllegalArgumentException("invalid project or name");
        }

        return artifact;
    }

    @Operation(
        summary = "Update if exist an artifact in a project context",
        description = "First check if project exist, if artifact exist update."
    )
    @PutMapping(
        value = "/{name}/{id}",
        consumes = { MediaType.APPLICATION_JSON_VALUE, "application/x-yaml" },
        produces = "application/json; charset=UTF-8"
    )
    public Artifact updateArtifact(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String name,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String id,
        @RequestBody @Valid @NotNull Artifact artifactDTO
    ) throws NoSuchEntityException {
        Artifact artifact = artifactService.getArtifact(id);

        //check for project and name match
        if (!artifact.getProject().equals(project) || !artifact.getName().equals(name)) {
            throw new IllegalArgumentException("invalid project or name");
        }

        return artifactService.updateArtifact(id, artifactDTO);
    }

    @Operation(
        summary = "Delete a specific artifact version",
        description = "First check if project exist, then delete a specific artifact version"
    )
    @DeleteMapping(path = "/{name}/{id}")
    public void deleteArtifact(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String name,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String id
    ) throws NoSuchEntityException {
        Artifact artifact = artifactService.getArtifact(id);

        //check for project and name match
        if (!artifact.getProject().equals(project) || !artifact.getName().equals(name)) {
            throw new IllegalArgumentException("invalid project or name");
        }

        artifactService.deleteArtifact(id);
    }
}
