package it.smartcommunitylabdhub.commons.models.accessors.utils;

import it.smartcommunitylabdhub.commons.models.entities.artifact.Artifact;

//TODO remove, this goes into the accessor
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
