package it.smartcommunitylabdhub.core.services;

import it.smartcommunitylabdhub.core.exceptions.CoreException;
import it.smartcommunitylabdhub.core.exceptions.CustomException;
import it.smartcommunitylabdhub.core.models.builders.artifact.ArtifactDTOBuilder;
import it.smartcommunitylabdhub.core.models.builders.artifact.ArtifactEntityBuilder;
import it.smartcommunitylabdhub.core.models.entities.artifact.Artifact;
import it.smartcommunitylabdhub.core.models.entities.artifact.ArtifactEntity;
import it.smartcommunitylabdhub.core.models.enums.State;
import it.smartcommunitylabdhub.core.models.filters.abstracts.AbstractSpecificationService;
import it.smartcommunitylabdhub.core.models.filters.entities.ArtifactEntityFilter;
import it.smartcommunitylabdhub.core.repositories.ArtifactRepository;
import it.smartcommunitylabdhub.core.services.interfaces.ArtifactService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class ArtifactServiceImpl extends AbstractSpecificationService<ArtifactEntity, ArtifactEntityFilter>
        implements ArtifactService {

    @Autowired
    ArtifactRepository artifactRepository;

    @Autowired
    ArtifactEntityBuilder artifactEntityBuilder;

    @Autowired
    ArtifactEntityFilter artifactEntityFilter;

    @Autowired
    ArtifactDTOBuilder artifactDTOBuilder;

    @Override
    public Page<Artifact> getArtifacts(Map<String, String> filter, Pageable pageable) {
        try {

            artifactEntityFilter.setName(filter.get("name"));
            artifactEntityFilter.setKind(filter.get("kind"));
            artifactEntityFilter.setCreatedDate(filter.get("created"));
            Optional<State> stateOptional = Stream.of(State.values())
                    .filter(state -> state.name().equals(filter.get("state")))
                    .findAny();
            artifactEntityFilter.setState(stateOptional.map(Enum::name).orElse(null));

            Specification<ArtifactEntity> specification = createSpecification(filter, artifactEntityFilter);

            Page<ArtifactEntity> artifactPage = this.artifactRepository.findAll(specification, pageable);

            return new PageImpl<>(
                    artifactPage.getContent()
                            .stream()
                            .map((artifact) -> artifactDTOBuilder.build(artifact, false))
                            .collect(Collectors.toList()),
                    pageable,
                    artifactPage.getContent().size()
            );

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
        Optional<ArtifactEntity> savedArtifact = Optional.of(artifactDTO)
                .map(artifactEntityBuilder::build)
                .map(this.artifactRepository::saveAndFlush);

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
                        artifactRepository.saveAndFlush(artifactUpdated);
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
