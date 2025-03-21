package it.smartcommunitylabdhub.artifacts.service;

import it.smartcommunitylabdhub.artifacts.persistence.ArtifactEntity;
import it.smartcommunitylabdhub.commons.exceptions.SystemException;
import it.smartcommunitylabdhub.commons.models.artifact.Artifact;
import it.smartcommunitylabdhub.commons.models.queries.SearchFilter;
import it.smartcommunitylabdhub.commons.services.ArtifactService;
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
    Page<Artifact> searchArtifacts(Pageable pageable, @Nullable SearchFilter<ArtifactEntity> filter)
        throws SystemException;

    /**
     * List the latest version of every artifact, with optional filters
     * @param pageable
     * @param filter
     * @return
     */
    Page<Artifact> searchLatestArtifacts(Pageable pageable, @Nullable SearchFilter<ArtifactEntity> filter)
        throws SystemException;

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
    ) throws SystemException;

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
    ) throws SystemException;
}
