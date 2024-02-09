package it.smartcommunitylabdhub.core.models.accessors.utils;

import it.smartcommunitylabdhub.commons.models.entities.artifact.Artifact;

public class ArtifactUtils {

  private ArtifactUtils() {}

  public static String getKey(Artifact artifactDTO) {
    return (
      "store://" +
      artifactDTO.getProject() +
      "/artifacts/" +
      artifactDTO.getKind() +
      "/" +
      artifactDTO.getName() +
      ":" +
      artifactDTO.getId()
    );
  }
}
