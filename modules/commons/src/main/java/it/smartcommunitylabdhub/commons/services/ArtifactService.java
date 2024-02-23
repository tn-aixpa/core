package it.smartcommunitylabdhub.commons.services;

import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.models.entities.artifact.Artifact;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArtifactService {
    Page<Artifact> getArtifacts(Map<String, String> filter, Pageable pageable);

    Artifact createArtifact(@NotNull Artifact artifactDTO) throws NoSuchEntityException, DuplicatedEntityException;

    Artifact getArtifact(@NotNull String id) throws NoSuchEntityException;

    Artifact updateArtifact(@NotNull String id, @NotNull Artifact artifactDTO);

    void deleteArtifact(@NotNull String uuid);
}
