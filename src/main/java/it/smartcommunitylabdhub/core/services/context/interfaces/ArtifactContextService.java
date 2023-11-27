package it.smartcommunitylabdhub.core.services.context.interfaces;

import it.smartcommunitylabdhub.core.models.entities.artifact.Artifact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArtifactContextService {

    Artifact createArtifact(String projectName, Artifact artifactDTO);

    Page<Artifact> getByProjectNameAndArtifactName(
            String projectName, String artifactName, Pageable pageable);

    Page<Artifact> getLatestByProjectName(
            String projectName, Pageable pageable);

    Artifact getByProjectAndArtifactAndUuid(
            String projectName, String artifactName, String uuid);

    Artifact getLatestByProjectNameAndArtifactName(
            String projectName, String artifactName);

    Artifact createOrUpdateArtifact(String projectName, String artifactName,
                                    Artifact artifactDTO);

    Artifact updateArtifact(String projectName, String artifactName, String uuid,
                            Artifact artifactDTO);

    Boolean deleteSpecificArtifactVersion(String projectName, String artifactName, String uuid);

    Boolean deleteAllArtifactVersions(String projectName, String artifactName);
}
