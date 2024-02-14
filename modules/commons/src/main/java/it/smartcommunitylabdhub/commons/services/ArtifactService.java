package it.smartcommunitylabdhub.commons.services;

import it.smartcommunitylabdhub.commons.models.entities.artifact.Artifact;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArtifactService {
    Page<Artifact> getArtifacts(Map<String, String> filter, Pageable pageable);

    Artifact createArtifact(Artifact artifactDTO);

    Artifact getArtifact(String uuid);

    Artifact updateArtifact(Artifact artifactDTO, String uuid);

    boolean deleteArtifact(String uuid);
}
