package it.smartcommunitylabdhub.core.services;

import it.smartcommunitylabdhub.core.exceptions.CoreException;
import it.smartcommunitylabdhub.core.exceptions.CustomException;
import it.smartcommunitylabdhub.core.models.builders.artifact.ArtifactDTOBuilder;
import it.smartcommunitylabdhub.core.models.builders.artifact.ArtifactEntityBuilder;
import it.smartcommunitylabdhub.core.models.entities.artifact.Artifact;
import it.smartcommunitylabdhub.core.models.entities.artifact.ArtifactEntity;
import it.smartcommunitylabdhub.core.repositories.ArtifactRepository;
import it.smartcommunitylabdhub.core.services.interfaces.ArtifactService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ArtifactServiceImpl implements ArtifactService {

    @Autowired
    ArtifactRepository artifactRepository;

    @Autowired
    ArtifactEntityBuilder artifactEntityBuilder;

    @Autowired
    ArtifactDTOBuilder artifactDTOBuilder;

    @Override
    public List<Artifact> getArtifacts(Pageable pageable) {
        try {
            Page<ArtifactEntity> artifactPage = this.artifactRepository.findAll(pageable);
            return artifactPage.getContent().stream().map((artifact) -> {
                return artifactDTOBuilder.build(artifact, false);
            }).collect(Collectors.toList());
        } catch (CustomException e) {
            throw new CoreException(
                    "InternalServerError",
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    public Artifact createArtifact(Artifact artifactDTO) {
        if (artifactDTO.getId() != null && artifactRepository.existsById(artifactDTO.getId())) {
            throw new CoreException("DuplicateArtifactId",
                    "Cannot create the artifact", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        Optional<ArtifactEntity> savedArtifact = Optional.ofNullable(artifactDTO)
                .map(artifactEntityBuilder::build)
                .map(this.artifactRepository::save);

        return savedArtifact.map(artifact -> artifactDTOBuilder.build(artifact, false))
                .orElseThrow(() -> new CoreException(
                        "InternalServerError",
                        "Error saving artifact",
                        HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Override
    public Artifact getArtifact(String uuid) {
        return artifactRepository.findById(uuid)
                .map(artifact -> {
                    try {
                        return artifactDTOBuilder.build(artifact, false);
                    } catch (CustomException e) {
                        throw new CoreException(
                                "InternalServerError",
                                e.getMessage(),
                                HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                })
                .orElseThrow(() -> new CoreException(
                        "ArtifactNotFound",
                        "The artifact you are searching for does not exist.",
                        HttpStatus.NOT_FOUND));
    }

    @Override
    public Artifact updateArtifact(Artifact artifactDTO, String uuid) {
        if (!artifactDTO.getId().equals(uuid)) {
            throw new CoreException(
                    "ArtifactNotMatch",
                    "Trying to update an artifact with a UUID different from the one passed in the request.",
                    HttpStatus.NOT_FOUND);
        }

        return artifactRepository.findById(uuid)
                .map(artifact -> {
                    try {
                        ArtifactEntity artifactUpdated =
                                artifactEntityBuilder.update(artifact, artifactDTO);
                        artifactRepository.save(artifactUpdated);
                        return artifactDTOBuilder.build(artifactUpdated, false);
                    } catch (CustomException e) {
                        throw new CoreException(
                                "InternalServerError",
                                e.getMessage(),
                                HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                })
                .orElseThrow(() -> new CoreException(
                        "ArtifactNotFound",
                        "The artifact you are searching for does not exist.",
                        HttpStatus.NOT_FOUND));
    }

    @Override
    public boolean deleteArtifact(String uuid) {
        try {
            if (this.artifactRepository.existsById(uuid)) {
                this.artifactRepository.deleteById(uuid);
                return true;
            }
            throw new CoreException(
                    "ArtifactNotFound",
                    "The artifact you are trying to delete does not exist.",
                    HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            throw new CoreException(
                    "InternalServerError",
                    "cannot delete artifact",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
