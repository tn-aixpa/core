package it.smartcommunitylabdhub.core.models.queries.services;

import it.smartcommunitylabdhub.commons.models.entities.artifact.Artifact;
import it.smartcommunitylabdhub.commons.models.queries.SearchFilter;
import it.smartcommunitylabdhub.commons.services.entities.ArtifactService;
import it.smartcommunitylabdhub.core.models.entities.artifact.ArtifactEntity;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/*
 * Searchable service for managing artifact
 */
public interface SearchableArtifactService extends ArtifactService {
    /**
     * List all artifacts, with optional filters
     * @param pageable
     * @param filter
     * @return
     */
    Page<Artifact> searchArtifacts(Pageable pageable, @Nullable SearchFilter<ArtifactEntity> filter);

    /**
     * List all versions of every artifact, with optional filters
     * @param project
     * @param pageable
     * @param filter
     * @return
     */
    Page<Artifact> searchArtifactsByProject(
        @NotNull String project,
        Pageable pageable,
        @Nullable SearchFilter<ArtifactEntity> filter
    );

    /**
     * List the latest version of every artifact, with optional filters
     * @param project
     * @param pageable
     * @param filter
     * @return
     */
    Page<Artifact> searchLatestArtifactsByProject(
        @NotNull String project,
        Pageable pageable,
        @Nullable SearchFilter<ArtifactEntity> filter
    );
}
