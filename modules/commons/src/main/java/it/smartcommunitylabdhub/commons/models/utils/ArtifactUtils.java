package it.smartcommunitylabdhub.commons.models.utils;

import it.smartcommunitylabdhub.commons.models.entities.artifact.Artifact;

//TODO remove, this goes into the accessor
@Deprecated(forRemoval = true)
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
