package it.smartcommunitylabdhub.core.services.interfaces;

import it.smartcommunitylabdhub.core.models.entities.artifact.Artifact;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ArtifactService {
    List<Artifact> getArtifacts(Pageable pageable);

    Artifact createArtifact(Artifact artifactDTO);

    Artifact getArtifact(String uuid);

    Artifact updateArtifact(Artifact artifactDTO, String uuid);

    boolean deleteArtifact(String uuid);

}
