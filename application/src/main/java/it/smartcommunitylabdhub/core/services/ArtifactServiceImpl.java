package it.smartcommunitylabdhub.core.services;

import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.models.entities.artifact.Artifact;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.queries.SearchFilter;
import it.smartcommunitylabdhub.core.models.entities.artifact.ArtifactEntity;
import it.smartcommunitylabdhub.core.models.entities.artifact.ArtifactEntity_;
import it.smartcommunitylabdhub.core.models.queries.services.SearchableArtifactService;
import it.smartcommunitylabdhub.core.models.queries.specifications.CommonSpecification;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class ArtifactServiceImpl implements SearchableArtifactService {

    @Autowired
    private EntityService<Artifact, ArtifactEntity> entityService;

    @Override
    public Page<Artifact> listArtifacts(Pageable pageable) {
        log.debug("list artifacts page {}", pageable);

        return entityService.list(pageable);
    }

    @Override
    public Page<Artifact> searchArtifacts(Pageable pageable, @Nullable SearchFilter<ArtifactEntity> filter) {
        log.debug("search artifacts page {}, filter {}", pageable, String.valueOf(filter));

        Specification<ArtifactEntity> specification = filter != null ? filter.toSpecification() : null;
        if (specification != null) {
            return entityService.search(specification, pageable);
        } else {
            return entityService.list(pageable);
        }
    }

    @Override
    public Page<Artifact> listLatestArtifactsByProject(@NotNull String project, Pageable pageable) {
        log.debug("list artifacts for project {}  page {}", project, pageable);
        Specification<ArtifactEntity> specification = Specification.allOf(
            CommonSpecification.projectEquals(project),
            CommonSpecification.latestByProject(project)
        );

        return entityService.search(specification, pageable);
    }

    @Override
    public Page<Artifact> searchLatestArtifactsByProject(
        @NotNull String project,
        Pageable pageable,
        @Nullable SearchFilter<ArtifactEntity> filter
    ) {
        log.debug("list artifacts for project {} with {} page {}", project, String.valueOf(filter), pageable);
        Specification<ArtifactEntity> filterSpecification = filter != null ? filter.toSpecification() : null;
        Specification<ArtifactEntity> specification = Specification.allOf(
            CommonSpecification.projectEquals(project),
            CommonSpecification.latestByProject(project),
            filterSpecification
        );

        return entityService.search(specification, pageable);
    }

    @Override
    public List<Artifact> findArtifacts(@NotNull String project, @NotNull String name) {
        log.debug("find artifacts for project {} with name {}", project, name);

        //fetch all versions ordered by date DESC
        Specification<ArtifactEntity> where = Specification.allOf(
            CommonSpecification.projectEquals(project),
            CommonSpecification.nameEquals(name)
        );
        Specification<ArtifactEntity> specification = (root, query, builder) -> {
            query.orderBy(builder.desc(root.get(ArtifactEntity_.CREATED)));
            return where.toPredicate(root, query, builder);
        };

        return entityService.searchAll(specification);
    }

    @Override
    public Page<Artifact> findArtifacts(@NotNull String project, @NotNull String name, Pageable pageable) {
        log.debug("find artifacts for project {} with name {} page {}", project, name, pageable);

        //fetch all versions ordered by date DESC
        Specification<ArtifactEntity> where = Specification.allOf(
            CommonSpecification.projectEquals(project),
            CommonSpecification.nameEquals(name)
        );
        Specification<ArtifactEntity> specification = (root, query, builder) -> {
            query.orderBy(builder.desc(root.get(ArtifactEntity_.CREATED)));
            return where.toPredicate(root, query, builder);
        };

        return entityService.search(specification, pageable);
    }

    @Override
    public Artifact findArtifact(@NotNull String id) {
        log.debug("find artifact with id {}", String.valueOf(id));

        return entityService.find(id);
    }

    @Override
    public Artifact getArtifact(@NotNull String id) throws NoSuchEntityException {
        log.debug("get artifact with id {}", String.valueOf(id));

        try {
            return entityService.get(id);
        } catch (NoSuchEntityException e) {
            throw new NoSuchEntityException(EntityName.ARTIFACT.toString());
        }
    }

    @Override
    public Artifact getLatestArtifact(@NotNull String project, @NotNull String name) throws NoSuchEntityException {
        log.debug("get latest artifact for project {} with name {}", project, name);

        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getLatestArtifact'");
    }

    @Override
    public Artifact createArtifact(@NotNull Artifact artifactDTO) throws DuplicatedEntityException {
        log.debug("create artifact");

        try {
            return entityService.create(artifactDTO);
        } catch (DuplicatedEntityException e) {
            throw new DuplicatedEntityException(EntityName.ARTIFACT.toString(), artifactDTO.getId());
        }
    }

    @Override
    public Artifact updateArtifact(@NotNull String id, @NotNull Artifact artifactDTO) throws NoSuchEntityException {
        log.debug("update artifact with id {}", String.valueOf(id));
        try {
            //fetch current and merge
            Artifact current = entityService.get(id);

            //spec is not modificable: enforce current
            artifactDTO.setSpec(current.getSpec());

            //update
            return entityService.update(id, artifactDTO);
        } catch (NoSuchEntityException e) {
            throw new NoSuchEntityException(EntityName.ARTIFACT.toString());
        }
    }

    @Override
    public void deleteArtifact(@NotNull String id) {
        log.debug("delete artifact with id {}", String.valueOf(id));

        entityService.delete(id);
    }

    @Override
    public void deleteArtifacts(@NotNull String project, @NotNull String name) {
        log.debug("delete artifacts for project {} with name {}", project, name);

        Specification<ArtifactEntity> spec = Specification.allOf(
            CommonSpecification.projectEquals(project),
            CommonSpecification.nameEquals(name)
        );

        long count = entityService.deleteAll(spec);
        log.debug("deleted count {}", count);
    }
}
