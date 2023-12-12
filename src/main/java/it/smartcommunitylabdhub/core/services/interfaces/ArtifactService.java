package it.smartcommunitylabdhub.core.services.interfaces;

import it.smartcommunitylabdhub.core.models.entities.artifact.Artifact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface ArtifactService {
    Page<Artifact> getArtifacts(Map<String, String> filter, Pageable pageable);

    Artifact createArtifact(Artifact artifactDTO);

    Artifact getArtifact(String uuid);

    Artifact updateArtifact(Artifact artifactDTO, String uuid);

    boolean deleteArtifact(String uuid);

}
