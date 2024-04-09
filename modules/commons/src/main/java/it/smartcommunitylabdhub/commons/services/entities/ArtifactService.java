package it.smartcommunitylabdhub.commons.services.entities;

import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.models.entities.artifact.Artifact;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

/*
 * Service for managing artifacts
 */
public interface ArtifactService {
    /**
     * List all artifacts
     * @param pageable
     * @return
     */
    Page<Artifact> listArtifacts(Pageable pageable);

    /**
     * List all versions of every artifact for a user
     * @param user
     * @return
     */
    List<Artifact> listArtifactsByUser(@NotNull String user);

    /**
     * List all versions of every artifact for a project
     * @param project
     * @return
     */
    List<Artifact> listArtifactsByProject(@NotNull String project);

    /**
     * List all versions of every artifact for a project
     * @param project
     * @param pageable
     * @return
     */
    Page<Artifact> listArtifactsByProject(@NotNull String project, Pageable pageable);

    /**
     * List the latest version of every artifact for a project
     * @param project
     * @return
     */
    List<Artifact> listLatestArtifactsByProject(@NotNull String project);

    /**
     * List the latest version of every artifact for a project
     * @param project
     * @param pageable
     * @return
     */
    Page<Artifact> listLatestArtifactsByProject(@NotNull String project, Pageable pageable);

    /**
     * Find all versions of a given artifact
     * @param project
     * @param name
     * @return
     */
    List<Artifact> findArtifacts(@NotNull String project, @NotNull String name);

    /**
     * Find all versions of a given artifact
     * @param project
     * @param name
     * @param pageable
     * @return
     */
    Page<Artifact> findArtifacts(@NotNull String project, @NotNull String name, Pageable pageable);

    /**
     * Find a specific artifact (version) via unique ID. Returns null if not found
     * @param id
     * @return
     */
    @Nullable
    Artifact findArtifact(@NotNull String id);

    /**
     * Get a specific artifact (version) via unique ID. Throws exception if not found
     * @param id
     * @return
     * @throws NoSuchEntityException
     */
    Artifact getArtifact(@NotNull String id) throws NoSuchEntityException;

    /**
     * Get the latest version of a given artifact
     * @param project
     * @param name
     * @return
     * @throws NoSuchEntityException
     */
    Artifact getLatestArtifact(@NotNull String project, @NotNull String name) throws NoSuchEntityException;

    /**
     * Create a new artifact and store it
     * @param artifactDTO
     * @return
     * @throws DuplicatedEntityException
     */
    Artifact createArtifact(@NotNull Artifact artifactDTO) throws DuplicatedEntityException;

    /**
     * Update a specific artifact version
     * @param id
     * @param artifactDTO
     * @return
     * @throws NoSuchEntityException
     */
    Artifact updateArtifact(@NotNull String id, @NotNull Artifact artifactDTO) throws NoSuchEntityException;

    /**
     * Delete a specific artifact (version) via unique ID
     * @param id
     */
    void deleteArtifact(@NotNull String id);

    /**
     * Delete all versions of a given artifact
     * @param project
     * @param name
     */
    void deleteArtifacts(@NotNull String project, @NotNull String name);

    /**
     * Delete all artifacts for a given project, with cascade.
     * @param project
     */
    void deleteArtifactsByProject(@NotNull String project);
}
