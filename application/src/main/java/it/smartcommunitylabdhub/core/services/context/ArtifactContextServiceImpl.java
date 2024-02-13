package it.smartcommunitylabdhub.core.services.context;

import it.smartcommunitylabdhub.commons.exceptions.CoreException;
import it.smartcommunitylabdhub.commons.exceptions.CustomException;
import it.smartcommunitylabdhub.commons.models.entities.artifact.Artifact;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.core.models.builders.artifact.ArtifactDTOBuilder;
import it.smartcommunitylabdhub.core.models.builders.artifact.ArtifactEntityBuilder;
import it.smartcommunitylabdhub.core.models.entities.artifact.ArtifactEntity;
import it.smartcommunitylabdhub.core.models.queries.filters.entities.ArtifactEntityFilter;
import it.smartcommunitylabdhub.core.models.queries.specifications.CommonSpecification;
import it.smartcommunitylabdhub.core.repositories.ArtifactRepository;
import it.smartcommunitylabdhub.core.services.context.interfaces.ArtifactContextService;
import jakarta.transaction.Transactional;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ArtifactContextServiceImpl
    extends ContextService<ArtifactEntity, ArtifactEntityFilter>
    implements ArtifactContextService {

    @Autowired
    ArtifactRepository artifactRepository;

    @Autowired
    ArtifactEntityBuilder artifactEntityBuilder;

    @Autowired
    ArtifactEntityFilter artifactEntityFilter;

    @Autowired
    ArtifactDTOBuilder artifactDTOBuilder;

    @Override
    public Artifact createArtifact(String projectName, Artifact artifactDTO) {
        try {
            // Check that project context is the same as the project passed to the
            // artifactDTO
            if (!projectName.equals(artifactDTO.getProject())) {
                throw new CustomException("Project Context and Artifact Project does not match", null);
            }

            // Check project context
            checkContext(artifactDTO.getProject());

            // Check if artifact already exist if exist throw exception otherwise create a
            // new one
            ArtifactEntity artifact = (ArtifactEntity) Optional
                .ofNullable(artifactDTO.getId())
                .flatMap(id ->
                    artifactRepository
                        .findById(id)
                        .map(a -> {
                            throw new CustomException(
                                "The project already contains an artifact with the specified UUID.",
                                null
                            );
                        })
                )
                .orElseGet(() -> {
                    // Build an artifact and store it in the database
                    ArtifactEntity newArtifact = artifactEntityBuilder.build(artifactDTO);
                    return artifactRepository.saveAndFlush(newArtifact);
                });

            // Return artifact DTO
            return artifactDTOBuilder.build(artifact, artifact.getEmbedded());
        } catch (CustomException e) {
            throw new CoreException("InternalServerError", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Page<Artifact> getLatestByProjectName(Map<String, String> filter, String projectName, Pageable pageable) {
        try {
            checkContext(projectName);

            artifactEntityFilter.setCreatedDate(filter.get("created"));
            artifactEntityFilter.setName(filter.get("name"));
            artifactEntityFilter.setKind(filter.get("kind"));
            Optional<State> stateOptional = Stream
                .of(State.values())
                .filter(state -> state.name().equals(filter.get("state")))
                .findAny();

            artifactEntityFilter.setState(stateOptional.map(Enum::name).orElse(null));

            Specification<ArtifactEntity> specification = createSpecification(filter, artifactEntityFilter)
                .and(CommonSpecification.latestByProject(projectName));

            Page<ArtifactEntity> artifactPage = artifactRepository.findAll(
                Specification
                    .where(specification)
                    .and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("project"), projectName)),
                pageable
            );

            return new PageImpl<>(
                artifactPage
                    .getContent()
                    .stream()
                    .map(artifact -> artifactDTOBuilder.build(artifact, artifact.getEmbedded()))
                    .collect(Collectors.toList()),
                pageable,
                artifactPage.getTotalElements()
            );
        } catch (CustomException e) {
            throw new CoreException("InternalServerError", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Page<Artifact> getByProjectNameAndArtifactName(
        Map<String, String> filter,
        String projectName,
        String artifactName,
        Pageable pageable
    ) {
        try {
            checkContext(projectName);

            artifactEntityFilter.setCreatedDate(filter.get("created"));
            artifactEntityFilter.setKind(filter.get("kind"));
            Optional<State> stateOptional = Stream
                .of(State.values())
                .filter(state -> state.name().equals(filter.get("state")))
                .findAny();

            artifactEntityFilter.setState(stateOptional.map(Enum::name).orElse(null));

            Specification<ArtifactEntity> specification = createSpecification(filter, artifactEntityFilter);

            Page<ArtifactEntity> artifactPage = artifactRepository.findAll(
                Specification
                    .where(specification)
                    .and((root, query, criteriaBuilder) ->
                        criteriaBuilder.and(
                            criteriaBuilder.equal(root.get("project"), projectName),
                            criteriaBuilder.equal(root.get("name"), artifactName)
                        )
                    ),
                pageable
            );

            return new PageImpl<>(
                artifactPage
                    .getContent()
                    .stream()
                    .map(artifact -> {
                        return artifactDTOBuilder.build(artifact, artifact.getEmbedded());
                    })
                    .collect(Collectors.toList()),
                pageable,
                artifactPage.getTotalElements()
            );
        } catch (CustomException e) {
            throw new CoreException("InternalServerError", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Artifact getByProjectAndArtifactAndUuid(String projectName, String artifactName, String uuid) {
        try {
            // Check project context
            checkContext(projectName);

            return this.artifactRepository.findByProjectAndNameAndId(projectName, artifactName, uuid)
                .map(artifact -> artifactDTOBuilder.build(artifact, artifact.getEmbedded()))
                .orElseThrow(() -> new CustomException("The artifact does not exist.", null));
        } catch (CustomException e) {
            throw new CoreException("InternalServerError", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Artifact getLatestByProjectNameAndArtifactName(String projectName, String artifactName) {
        try {
            // Check project context
            checkContext(projectName);

            return this.artifactRepository.findLatestArtifactByProjectAndName(projectName, artifactName)
                .map(artifact -> artifactDTOBuilder.build(artifact, artifact.getEmbedded()))
                .orElseThrow(() -> new CustomException("The artifact does not exist.", null));
        } catch (CustomException e) {
            throw new CoreException("InternalServerError", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Artifact createOrUpdateArtifact(String projectName, String artifactName, Artifact artifactDTO) {
        try {
            // Check that project context is the same as the project passed to the
            // artifactDTO
            if (!projectName.equals(artifactDTO.getProject())) {
                throw new CustomException("Project Context and Artifact Project does not match.", null);
            }
            if (!artifactName.equals(artifactDTO.getName())) {
                throw new CustomException(
                    "Trying to create/update an artifact with name different from the one passed in the request.",
                    null
                );
            }

            // Check project context
            checkContext(artifactDTO.getProject());

            // Check if artifact already exist if exist throw exception otherwise create a
            // new one
            ArtifactEntity artifact = Optional
                .ofNullable(artifactDTO.getId())
                .flatMap(id -> {
                    Optional<ArtifactEntity> optionalArtifact = artifactRepository.findById(id);
                    if (optionalArtifact.isPresent()) {
                        ArtifactEntity existingArtifact = optionalArtifact.get();

                        // Update the existing artifact version
                        final ArtifactEntity artifactUpdated = artifactEntityBuilder.update(
                            existingArtifact,
                            artifactDTO
                        );
                        return Optional.of(this.artifactRepository.saveAndFlush(artifactUpdated));
                    } else {
                        // Build a new artifact and store it in the database
                        ArtifactEntity newArtifact = artifactEntityBuilder.build(artifactDTO);
                        return Optional.of(artifactRepository.saveAndFlush(newArtifact));
                    }
                })
                .orElseGet(() -> {
                    // Build a new artifact and store it in the database
                    ArtifactEntity newArtifact = artifactEntityBuilder.build(artifactDTO);
                    return artifactRepository.saveAndFlush(newArtifact);
                });

            // Return artifact DTO
            return artifactDTOBuilder.build(artifact, artifact.getEmbedded());
        } catch (CustomException e) {
            throw new CoreException("InternalServerError", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Artifact updateArtifact(String projectName, String artifactName, String uuid, Artifact artifactDTO) {
        try {
            // Check that project context is the same as the project passed to the
            // artifactDTO
            if (!projectName.equals(artifactDTO.getProject())) {
                throw new CustomException("Project Context and Artifact Project does not match", null);
            }
            if (!uuid.equals(artifactDTO.getId())) {
                throw new CustomException(
                    "Trying to update an artifact with an ID different from the one passed in the request.",
                    null
                );
            }
            // Check project context
            checkContext(artifactDTO.getProject());

            ArtifactEntity artifact =
                this.artifactRepository.findById(artifactDTO.getId())
                    .map(a -> {
                        // Update the existing artifact version
                        return artifactEntityBuilder.update(a, artifactDTO);
                    })
                    .orElseThrow(() -> new CustomException("The artifact does not exist.", null));

            // Return artifact DTO
            return artifactDTOBuilder.build(artifact, artifact.getEmbedded());
        } catch (CustomException e) {
            throw new CoreException("InternalServerError", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public Boolean deleteSpecificArtifactVersion(String projectName, String artifactName, String uuid) {
        try {
            if (this.artifactRepository.existsByProjectAndNameAndId(projectName, artifactName, uuid)) {
                this.artifactRepository.deleteByProjectAndNameAndId(projectName, artifactName, uuid);
                return true;
            }
            throw new CoreException(
                "ArtifactNotFound",
                "The artifact you are trying to delete does not exist.",
                HttpStatus.NOT_FOUND
            );
        } catch (Exception e) {
            throw new CoreException("InternalServerError", "cannot delete artifact", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public Boolean deleteAllArtifactVersions(String projectName, String artifactName) {
        try {
            if (artifactRepository.existsByProjectAndName(projectName, artifactName)) {
                this.artifactRepository.deleteByProjectAndName(projectName, artifactName);
                return true;
            }
            throw new CoreException(
                "ArtifactNotFound",
                "The artifacts you are trying to delete does not exist.",
                HttpStatus.NOT_FOUND
            );
        } catch (Exception e) {
            throw new CoreException("InternalServerError", "cannot delete artifact", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
