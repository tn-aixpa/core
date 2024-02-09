package it.smartcommunitylabdhub.commons.services.interfaces;

import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import it.smartcommunitylabdhub.commons.models.entities.artifact.Artifact;

public interface ArtifactService {
  Page<Artifact> getArtifacts(Map<String, String> filter, Pageable pageable);

  Artifact createArtifact(Artifact artifactDTO);

  Artifact getArtifact(String uuid);

  Artifact updateArtifact(Artifact artifactDTO, String uuid);

  boolean deleteArtifact(String uuid);
}
