package it.smartcommunitylabdhub.commons.services;

import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.models.entities.artifact.Artifact;
import it.smartcommunitylabdhub.commons.models.queries.SearchFilter;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

public interface ArtifactService<T> {
    Page<Artifact> listArtifacts(Pageable pageable, @Nullable SearchFilter<T> filter);
    Page<Artifact> listLatestArtifactsByProject(
        @NotNull String project,
        Pageable pageable,
        @Nullable SearchFilter<T> filter
    );

    List<Artifact> findArtifacts(@NotNull String project, @NotNull String name);
    Page<Artifact> findArtifacts(@NotNull String project, @NotNull String name, Pageable pageable);

    Artifact getArtifact(@NotNull String id) throws NoSuchEntityException;
    Artifact getLatestArtifact(@NotNull String project, @NotNull String name) throws NoSuchEntityException;

    Artifact createArtifact(@NotNull Artifact artifactDTO) throws DuplicatedEntityException;
    Artifact updateArtifact(@NotNull String id, @NotNull Artifact artifactDTO) throws NoSuchEntityException;
    void deleteArtifact(@NotNull String id);
    void deleteArtifacts(@NotNull String project, @NotNull String name);
}
