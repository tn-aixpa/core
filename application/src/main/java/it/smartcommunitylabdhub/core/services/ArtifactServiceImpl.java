package it.smartcommunitylabdhub.core.services;

import it.smartcommunitylabdhub.commons.exceptions.CustomException;
import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.models.entities.artifact.Artifact;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.services.ArtifactService;
import it.smartcommunitylabdhub.core.exceptions.CoreException;
import it.smartcommunitylabdhub.core.models.builders.artifact.ArtifactDTOBuilder;
import it.smartcommunitylabdhub.core.models.builders.artifact.ArtifactEntityBuilder;
import it.smartcommunitylabdhub.core.models.entities.artifact.ArtifactEntity;
import it.smartcommunitylabdhub.core.models.queries.filters.abstracts.AbstractSpecificationService;
import it.smartcommunitylabdhub.core.models.queries.filters.entities.ArtifactEntityFilter;
import it.smartcommunitylabdhub.core.repositories.ArtifactRepository;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ArtifactServiceImpl extends BaseEntityServiceImpl<Artifact, ArtifactEntity> implements ArtifactService {

    protected ArtifactServiceImpl(
        ArtifactRepository artifactRepository,
        ArtifactEntityBuilder artifactEntityBuilder,
        ArtifactDTOBuilder artifactDTOBuilder
    ) {
        super(artifactRepository, artifactEntityBuilder, artifactDTOBuilder);
    }

    @Autowired
    ArtifactEntityFilter artifactEntityFilter;

    @Override
    public Page<Artifact> getArtifacts(Map<String, String> filter, Pageable pageable) {
        try {
            artifactEntityFilter.setName(filter.get("name"));
            artifactEntityFilter.setKind(filter.get("kind"));
            artifactEntityFilter.setCreatedDate(filter.get("created"));
            Optional<State> stateOptional = Stream
                .of(State.values())
                .filter(state -> state.name().equals(filter.get("state")))
                .findAny();
            artifactEntityFilter.setState(stateOptional.map(Enum::name).orElse(null));

            Specification<ArtifactEntity> specification = createSpecification(filter, artifactEntityFilter);
            return search(specification, pageable);
        } catch (CustomException e) {
            throw new CoreException("InternalServerError", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Artifact createArtifact(@NotNull Artifact artifactDTO)
        throws NoSuchEntityException, DuplicatedEntityException {
        try {
            return create(artifactDTO);
        } catch (NoSuchEntityException e) {
            throw new NoSuchEntityException(EntityName.ARTIFACT.toString());
        } catch (DuplicatedEntityException e) {
            throw new DuplicatedEntityException(EntityName.ARTIFACT.toString(), artifactDTO.getId());
        }
    }

    protected Specification<ArtifactEntity> createSpecification(
        Map<String, String> filter,
        ArtifactEntityFilter entityFilter
    ) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            // Add your custom filter based on the provided map
            predicate = entityFilter.toPredicate(root, query, criteriaBuilder);

            // Add more conditions for other filter if needed

            return predicate;
        };
    }

    @Override
    public Artifact update(@NotNull String id, @NotNull Artifact dto) throws NoSuchEntityException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public Artifact getArtifact(@NotNull String id) throws NoSuchEntityException {
        try {
            return get(id);
        } catch (NoSuchEntityException e) {
            throw new NoSuchEntityException(EntityName.ARTIFACT.toString());
        }
    }

    @Override
    public Artifact updateArtifact(@NotNull String id, @NotNull Artifact artifactDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateArtifact'");
    }

    @Override
    public void deleteArtifact(@NotNull String uuid) {
        delete(uuid);
    }
}
