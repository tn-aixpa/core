package it.smartcommunitylabdhub.core.services;

import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.models.entities.function.Function;
import it.smartcommunitylabdhub.commons.models.entities.project.Project;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.queries.SearchFilter;
import it.smartcommunitylabdhub.commons.models.specs.Spec;
import it.smartcommunitylabdhub.commons.services.SpecRegistry;
import it.smartcommunitylabdhub.commons.services.entities.TaskService;
import it.smartcommunitylabdhub.core.models.builders.function.FunctionEntityBuilder;
import it.smartcommunitylabdhub.core.models.entities.AbstractEntity_;
import it.smartcommunitylabdhub.core.models.entities.FunctionEntity;
import it.smartcommunitylabdhub.core.models.entities.ProjectEntity;
import it.smartcommunitylabdhub.core.models.indexers.FunctionEntityIndexer;
import it.smartcommunitylabdhub.core.models.indexers.IndexableFunctionService;
import it.smartcommunitylabdhub.core.models.queries.services.SearchableFunctionService;
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
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Transactional
@Slf4j
public class FunctionServiceImpl implements SearchableFunctionService, IndexableFunctionService {

    @Autowired
    private EntityService<Function, FunctionEntity> entityService;

    @Autowired
    private EntityService<Project, ProjectEntity> projectService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private FunctionEntityIndexer indexer;

    @Autowired
    private FunctionEntityBuilder entityBuilder;

    @Autowired
    SpecRegistry specRegistry;

    @Override
    public Page<Function> listFunctions(Pageable pageable) {
        log.debug("list functions page {}", pageable);

        return entityService.list(pageable);
    }

    @Override
    public List<Function> listFunctionsByUser(@NotNull String user) {
        log.debug("list all functions for user {}  ", user);

        return entityService.searchAll(CommonSpecification.createdByEquals(user));
    }

    @Override
    public Page<Function> searchFunctions(Pageable pageable, @Nullable SearchFilter<FunctionEntity> filter) {
        log.debug("list functions page {}, filter {}", pageable, String.valueOf(filter));

        Specification<FunctionEntity> specification = filter != null ? filter.toSpecification() : null;
        if (specification != null) {
            return entityService.search(specification, pageable);
        } else {
            return entityService.list(pageable);
        }
    }

    @Override
    public List<Function> listFunctionsByProject(@NotNull String project) {
        log.debug("list functions for project {}", project);
        Specification<FunctionEntity> specification = Specification.allOf(CommonSpecification.projectEquals(project));

        return entityService.searchAll(specification);
    }

    @Override
    public Page<Function> listFunctionsByProject(@NotNull String project, Pageable pageable) {
        log.debug("list functions for project {} page {}", project, pageable);
        Specification<FunctionEntity> specification = Specification.allOf(CommonSpecification.projectEquals(project));

        return entityService.search(specification, pageable);
    }

    @Override
    public List<Function> listLatestFunctionsByProject(@NotNull String project) {
        log.debug("list latest functions for project {}", project);
        Specification<FunctionEntity> specification = Specification.allOf(
            CommonSpecification.projectEquals(project),
            CommonSpecification.latestByProject(project)
        );

        return entityService.searchAll(specification);
    }

    @Override
    public Page<Function> listLatestFunctionsByProject(@NotNull String project, Pageable pageable) {
        log.debug("list latest functions for project {} page {}", project, pageable);
        Specification<FunctionEntity> specification = Specification.allOf(
            CommonSpecification.projectEquals(project),
            CommonSpecification.latestByProject(project)
        );

        return entityService.search(specification, pageable);
    }

    @Override
    public Page<Function> searchLatestFunctionsByProject(
        @NotNull String project,
        Pageable pageable,
        @Nullable SearchFilter<FunctionEntity> filter
    ) {
        log.debug("search latest functions for project {} with {} page {}", project, String.valueOf(filter), pageable);
        Specification<FunctionEntity> filterSpecification = filter != null ? filter.toSpecification() : null;
        Specification<FunctionEntity> specification = Specification.allOf(
            CommonSpecification.projectEquals(project),
            CommonSpecification.latestByProject(project),
            filterSpecification
        );

        return entityService.search(specification, pageable);
    }

    @Override
    public Page<Function> searchFunctionsByProject(
        @NotNull String project,
        Pageable pageable,
        @Nullable SearchFilter<FunctionEntity> filter
    ) {
        log.debug("search functions for project {} with {} page {}", project, String.valueOf(filter), pageable);
        Specification<FunctionEntity> filterSpecification = filter != null ? filter.toSpecification() : null;
        Specification<FunctionEntity> specification = Specification.allOf(
            CommonSpecification.projectEquals(project),
            filterSpecification
        );

        return entityService.search(specification, pageable);
    }

    @Override
    public List<Function> findFunctions(@NotNull String project, @NotNull String name) {
        log.debug("find functions for project {} with name {}", project, name);

        //fetch all versions ordered by date DESC
        Specification<FunctionEntity> where = Specification.allOf(
            CommonSpecification.projectEquals(project),
            CommonSpecification.nameEquals(name)
        );
        Specification<FunctionEntity> specification = (root, query, builder) -> {
            query.orderBy(builder.desc(root.get(AbstractEntity_.CREATED)));
            return where.toPredicate(root, query, builder);
        };

        return entityService.searchAll(specification);
    }

    @Override
    public Page<Function> findFunctions(@NotNull String project, @NotNull String name, Pageable pageable) {
        log.debug("find functions for project {} with name {} page {}", project, name, pageable);

        //fetch all versions ordered by date DESC
        Specification<FunctionEntity> where = Specification.allOf(
            CommonSpecification.projectEquals(project),
            CommonSpecification.nameEquals(name)
        );
        Specification<FunctionEntity> specification = (root, query, builder) -> {
            query.orderBy(builder.desc(root.get(AbstractEntity_.CREATED)));
            return where.toPredicate(root, query, builder);
        };

        return entityService.search(specification, pageable);
    }

    @Override
    public Function findFunction(@NotNull String id) {
        log.debug("find function with id {}", String.valueOf(id));

        return entityService.find(id);
    }

    @Override
    public Function getFunction(@NotNull String id) throws NoSuchEntityException {
        log.debug("get function with id {}", String.valueOf(id));

        try {
            return entityService.get(id);
        } catch (NoSuchEntityException e) {
            throw new NoSuchEntityException(EntityName.FUNCTION.toString());
        }
    }

    @Override
    public Function getLatestFunction(@NotNull String project, @NotNull String name) throws NoSuchEntityException {
        log.debug("get latest function for project {} with name {}", project, name);

        //fetch latest version ordered by date DESC
        Specification<FunctionEntity> specification = CommonSpecification.latestByProject(project, name);
        return entityService.searchAll(specification).stream().findFirst().orElseThrow(NoSuchEntityException::new);
    }

    @Override
    public Function createFunction(@NotNull Function dto) throws DuplicatedEntityException {
        log.debug("create function");
        if (log.isTraceEnabled()) {
            log.trace("dto: {}", dto);
        }

        //validate project
        String projectId = dto.getProject();
        if (!StringUtils.hasText(projectId) || projectService.find(projectId) == null) {
            throw new IllegalArgumentException("invalid or missing project");
        }

        // Parse and export Spec
        Spec spec = specRegistry.createSpec(dto.getKind(), EntityName.FUNCTION, dto.getSpec());
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
            throw new DuplicatedEntityException(EntityName.FUNCTION.toString(), dto.getId());
        }
    }

    @Override
    public Function updateFunction(@NotNull String id, @NotNull Function functionDTO) throws NoSuchEntityException {
        log.debug("update function with id {}", String.valueOf(id));
        try {
            //fetch current and merge
            Function current = entityService.get(id);

            //spec is not modificable: enforce current
            functionDTO.setSpec(current.getSpec());

            //update
            return entityService.update(id, functionDTO);
        } catch (NoSuchEntityException e) {
            throw new NoSuchEntityException(EntityName.FUNCTION.toString());
        }
    }

    @Override
    public void deleteFunction(@NotNull String id, @Nullable Boolean cascade) {
        log.debug("delete function with id {}", String.valueOf(id));

        Function function = findFunction(id);
        if (function != null) {
            if (Boolean.TRUE.equals(cascade)) {
                //tasks
                log.debug("cascade delete tasks for function with id {}", String.valueOf(id));
                taskService.deleteTasksByFunctionId(id);
            }

            //delete the function
            entityService.delete(id);
        }
    }

    @Override
    public void deleteFunctions(@NotNull String project, @NotNull String name) {
        log.debug("delete functions for project {} with name {}", project, name);

        //delete with cascade
        findFunctions(project, name).forEach(function -> deleteFunction(function.getId(), Boolean.TRUE));
    }

    @Override
    public void deleteFunctionsByProject(@NotNull String project) {
        log.debug("delete functions for project {}", project);

        entityService
            .searchAll(CommonSpecification.projectEquals(project))
            .forEach(f -> deleteFunction(f.getId(), Boolean.TRUE));
    }

    @Override
    public void indexFunction(@NotNull String id) {
        log.debug("index function with id {}", String.valueOf(id));

        Function function = entityService.get(id);
        indexer.index(entityBuilder.convert(function));
    }

    @Override
    public void reindexFunctions() {
        log.debug("reindex all functions");

        //clear index
        indexer.clearIndex();
        
        //use pagination and batch
        boolean hasMore = true;
        int pageNumber = 0;
        while (hasMore) {
            hasMore = false;

            try {
                Page<Function> page = entityService.list(PageRequest.of(pageNumber, 1000));
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
