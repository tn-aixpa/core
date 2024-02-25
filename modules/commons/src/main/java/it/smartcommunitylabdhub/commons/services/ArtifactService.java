package it.smartcommunitylabdhub.commons.services;

import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.models.entities.artifact.Artifact;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArtifactService {
    Page<Artifact> getArtifacts(Map<String, String> filter, Pageable pageable);
    Page<Artifact> getLatestArtifactsByProject(@NotNull String project, Map<String, String> filter, Pageable pageable);

    List<Artifact> findArtifacts(@NotNull String project, @NotNull String name);
    Page<Artifact> findArtifacts(@NotNull String project, @NotNull String name, Pageable pageable);

    Artifact getArtifact(@NotNull String id) throws NoSuchEntityException;
    Artifact getLatestArtifact(@NotNull String project, @NotNull String name) throws NoSuchEntityException;

    Artifact createArtifact(@NotNull Artifact artifactDTO) throws DuplicatedEntityException;
    Artifact updateArtifact(@NotNull String id, @NotNull Artifact artifactDTO) throws NoSuchEntityException;
    void deleteArtifact(@NotNull String uuid);
    void deleteArtifacts(@NotNull String project, @NotNull String name);
}
