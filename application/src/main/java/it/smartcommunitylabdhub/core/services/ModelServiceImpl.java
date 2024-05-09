package it.smartcommunitylabdhub.core.services;

import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.models.entities.model.Model;
import it.smartcommunitylabdhub.commons.models.entities.project.Project;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.queries.SearchFilter;
import it.smartcommunitylabdhub.commons.models.specs.Spec;
import it.smartcommunitylabdhub.commons.services.SpecRegistry;
import it.smartcommunitylabdhub.core.models.builders.model.ModelEntityBuilder;
import it.smartcommunitylabdhub.core.models.entities.AbstractEntity_;
import it.smartcommunitylabdhub.core.models.entities.ModelEntity;
import it.smartcommunitylabdhub.core.models.entities.ProjectEntity;
import it.smartcommunitylabdhub.core.models.indexers.IndexableModelService;
import it.smartcommunitylabdhub.core.models.indexers.ModelEntityIndexer;
import it.smartcommunitylabdhub.core.models.queries.services.SearchableModelService;
import it.smartcommunitylabdhub.core.models.queries.specifications.CommonSpecification;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Transactional
@Slf4j
public class ModelServiceImpl implements SearchableModelService, IndexableModelService {

    @Autowired
    private EntityService<Model, ModelEntity> entityService;

    @Autowired
    private EntityService<Project, ProjectEntity> projectService;

    @Autowired
    private ModelEntityIndexer indexer;

    @Autowired
    private ModelEntityBuilder entityBuilder;

    @Autowired
    SpecRegistry specRegistry;

    @Override
    public Page<Model> listModels(Pageable pageable) {
        log.debug("list models page {}", pageable);

        return entityService.list(pageable);
    }

    @Override
    public List<Model> listModelsByUser(@NotNull String user) {
        log.debug("list all models for user {}", user);

        return entityService.searchAll(CommonSpecification.createdByEquals(user));
    }

    @Override
    public Page<Model> searchModels(Pageable pageable, SearchFilter<ModelEntity> filter) {
        log.debug("list models page {}, filter {}", pageable, String.valueOf(filter));

        Specification<ModelEntity> specification = filter != null ? filter.toSpecification() : null;
        if (specification != null) {
            return entityService.search(specification, pageable);
        } else {
            return entityService.list(pageable);
        }
    }

    @Override
    public List<Model> listModelsByProject(@NotNull String project) {
        log.debug("list all models for project {}", project);
        Specification<ModelEntity> specification = Specification.allOf(CommonSpecification.projectEquals(project));

        return entityService.searchAll(specification);
    }

    @Override
    public Page<Model> listModelsByProject(@NotNull String project, Pageable pageable) {
        log.debug("list all models for project {} page {}", project, pageable);
        Specification<ModelEntity> specification = Specification.allOf(CommonSpecification.projectEquals(project));

        return entityService.search(specification, pageable);
    }

    @Override
    public List<Model> listLatestModelsByProject(@NotNull String project) {
        log.debug("list latest models for project {}", project);
        Specification<ModelEntity> specification = Specification.allOf(
            CommonSpecification.projectEquals(project),
            CommonSpecification.latestByProject(project)
        );

        return entityService.searchAll(specification);
    }

    @Override
    public Page<Model> listLatestModelsByProject(@NotNull String project, Pageable pageable) {
        log.debug("list latest models for project {} page {}", project, pageable);
        Specification<ModelEntity> specification = Specification.allOf(
            CommonSpecification.projectEquals(project),
            CommonSpecification.latestByProject(project)
        );

        return entityService.search(specification, pageable);
    }

    @Override
    public Page<Model> searchModelsByProject(
        @NotNull String project,
        Pageable pageable,
        SearchFilter<ModelEntity> filter
    ) {
        log.debug("search all models for project {} with {} page {}", project, String.valueOf(filter), pageable);
        Specification<ModelEntity> filterSpecification = filter != null ? filter.toSpecification() : null;
        Specification<ModelEntity> specification = Specification.allOf(
            CommonSpecification.projectEquals(project),
            filterSpecification
        );

        return entityService.search(specification, pageable);
    }

    @Override
    public Page<Model> searchLatestModelsByProject(
        @NotNull String project,
        Pageable pageable,
        SearchFilter<ModelEntity> filter
    ) {
        log.debug("search latest models for project {} with {} page {}", project, String.valueOf(filter), pageable);
        Specification<ModelEntity> filterSpecification = filter != null ? filter.toSpecification() : null;
        Specification<ModelEntity> specification = Specification.allOf(
            CommonSpecification.projectEquals(project),
            CommonSpecification.latestByProject(project),
            filterSpecification
        );

        return entityService.search(specification, pageable);
    }

    @Override
    public List<Model> findModels(@NotNull String project, @NotNull String name) {
        log.debug("find models for project {} with name {}", project, name);

        //fetch all versions ordered by date DESC
        Specification<ModelEntity> where = Specification.allOf(
            CommonSpecification.projectEquals(project),
            CommonSpecification.nameEquals(name)
        );

        Specification<ModelEntity> specification = (root, query, builder) -> {
            query.orderBy(builder.desc(root.get(AbstractEntity_.CREATED)));
            return where.toPredicate(root, query, builder);
        };

        return entityService.searchAll(specification);
    }

    @Override
    public Page<Model> findModels(@NotNull String project, @NotNull String name, Pageable pageable) {
        log.debug("find models for project {} with name {} page {}", project, name, pageable);

        //fetch all versions ordered by date DESC
        Specification<ModelEntity> where = Specification.allOf(
            CommonSpecification.projectEquals(project),
            CommonSpecification.nameEquals(name)
        );
        Specification<ModelEntity> specification = (root, query, builder) -> {
            query.orderBy(builder.desc(root.get(AbstractEntity_.CREATED)));
            return where.toPredicate(root, query, builder);
        };

        return entityService.search(specification, pageable);
    }

    @Override
    public Model findModel(@NotNull String id) {
        log.debug("find model with id {}", String.valueOf(id));

        return entityService.find(id);
    }

    @Override
    public Model getModel(@NotNull String id) throws NoSuchEntityException {
        log.debug("get model with id {}", String.valueOf(id));

        try {
            return entityService.get(id);
        } catch (NoSuchEntityException e) {
            throw new NoSuchEntityException(EntityName.MODEL.toString());
        }
    }

    @Override
    public Model getLatestModel(@NotNull String project, @NotNull String name) throws NoSuchEntityException {
        log.debug("get latest model for project {} with name {}", project, name);

        //fetch latest version ordered by date DESC
        Specification<ModelEntity> specification = CommonSpecification.latestByProject(project, name);
        return entityService.searchAll(specification).stream().findFirst().orElseThrow(NoSuchEntityException::new);
    }

    @Override
    public Model createModel(@NotNull Model dto) throws DuplicatedEntityException {
        log.debug("create model");
        if (log.isTraceEnabled()) {
            log.trace("dto: {}", dto);
        }

        //validate project
        String projectId = dto.getProject();
        if (!StringUtils.hasText(projectId) || projectService.find(projectId) == null) {
            throw new IllegalArgumentException("invalid or missing project");
        }

        // Parse and export Spec
        Spec spec = specRegistry.createSpec(dto.getKind(), dto.getSpec());
        if (spec == null) {
            throw new IllegalArgumentException("invalid kind");
        }

        //TODO validate

        //update spec as exported
        dto.setSpec(spec.toMap());

        try {
            if (log.isTraceEnabled()) {
                log.trace("storable dto: {}", dto);
            }

            return entityService.create(dto);
        } catch (DuplicatedEntityException e) {
            throw new DuplicatedEntityException(EntityName.MODEL.toString(), dto.getId());
        }
    }

    @Override
    public Model updateModel(@NotNull String id, @NotNull Model modelDTO) throws NoSuchEntityException {
        log.debug("model model with id {}", String.valueOf(id));
        try {
            //fetch current and merge
            Model current = entityService.get(id);

            //spec is not modificable: enforce current
            modelDTO.setSpec(current.getSpec());

            //update
            return entityService.update(id, modelDTO);
        } catch (NoSuchEntityException e) {
            throw new NoSuchEntityException(EntityName.MODEL.toString());
        }
    }

    @Override
    public void deleteModel(@NotNull String id) {
        log.debug("delete model with id {}", String.valueOf(id));

        entityService.delete(id);
    }

    @Override
    public void deleteModels(@NotNull String project, @NotNull String name) {
        log.debug("delete models for project {} with name {}", project, name);

        Specification<ModelEntity> spec = Specification.allOf(
            CommonSpecification.projectEquals(project),
            CommonSpecification.nameEquals(name)
        );

        long count = entityService.deleteAll(spec);
        log.debug("deleted count {}", count);
    }

    @Override
    public void deleteModelsByProject(@NotNull String project) {
        log.debug("delete models for project {}", project);

        entityService.deleteAll(CommonSpecification.projectEquals(project));
    }

    @Override
    public void indexModel(@NotNull String id) {
        log.debug("index model with id {}", String.valueOf(id));

        Model model = entityService.get(id);
        indexer.index(entityBuilder.convert(model));
    }

    @Override
    public void reindexModels() {
        log.debug("reindex all models");

        //clear index
        indexer.clearIndex();

        //use pagination and batch
        boolean hasMore = true;
        int pageNumber = 0;
        while (hasMore) {
            hasMore = false;

            try {
                Page<Model> page = entityService.list(PageRequest.of(pageNumber, 1000));
                indexer.indexAll(
                    page.getContent().stream().map(e -> entityBuilder.convert(e)).collect(Collectors.toList())
                );
                hasMore = page.hasNext();
            } catch (Exception e) {
                hasMore = false;

                log.error("error with indexing: {}", e.getMessage());
            }
        }
    }
}
