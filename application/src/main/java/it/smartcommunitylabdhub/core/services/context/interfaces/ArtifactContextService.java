package it.smartcommunitylabdhub.core.services.context.interfaces;

import it.smartcommunitylabdhub.commons.models.entities.artifact.Artifact;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArtifactContextService {
    Artifact createArtifact(String projectName, Artifact artifactDTO);

    Page<Artifact> getByProjectNameAndArtifactName(
        Map<String, String> filter,
        String projectName,
        String artifactName,
        Pageable pageable
    );

    Page<Artifact> getLatestByProjectName(Map<String, String> filter, String projectName, Pageable pageable);

    Artifact getByProjectAndArtifactAndUuid(String projectName, String artifactName, String uuid);

    Artifact getLatestByProjectNameAndArtifactName(String projectName, String artifactName);

    Artifact createOrUpdateArtifact(String projectName, String artifactName, Artifact artifactDTO);

    Artifact updateArtifact(String projectName, String artifactName, String uuid, Artifact artifactDTO);

    Boolean deleteSpecificArtifactVersion(String projectName, String artifactName, String uuid);

    Boolean deleteAllArtifactVersions(String projectName, String artifactName);
}
