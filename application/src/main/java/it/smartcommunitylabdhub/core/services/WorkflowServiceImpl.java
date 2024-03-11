package it.smartcommunitylabdhub.core.services;

import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.models.entities.workflow.Workflow;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.queries.SearchFilter;
import it.smartcommunitylabdhub.commons.models.specs.Spec;
import it.smartcommunitylabdhub.commons.services.SpecRegistry;
import it.smartcommunitylabdhub.core.models.entities.workflow.WorkflowEntity;
import it.smartcommunitylabdhub.core.models.entities.workflow.WorkflowEntity_;
import it.smartcommunitylabdhub.core.models.queries.services.SearchableWorkflowService;
import it.smartcommunitylabdhub.core.models.queries.specifications.CommonSpecification;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
@Transactional
@Slf4j
public class WorkflowServiceImpl implements SearchableWorkflowService {

    @Autowired
    private EntityService<Workflow, WorkflowEntity> entityService;

    @Autowired
    SpecRegistry specRegistry;

    @Override
    public Page<Workflow> listWorkflows(Pageable pageable) {
        log.debug("list workflows page {}", pageable);

        return entityService.list(pageable);
    }

    @Override
    public Page<Workflow> searchWorkflows(Pageable pageable, @Nullable SearchFilter<WorkflowEntity> filter) {
        log.debug("search workflows page {}, filter {}", pageable, String.valueOf(filter));

        Specification<WorkflowEntity> specification = filter != null ? filter.toSpecification() : null;
        if (specification != null) {
            return entityService.search(specification, pageable);
        } else {
            return entityService.list(pageable);
        }
    }

    @Override
    public List<Workflow> listWorkflowsByProject(@NotNull String project) {
        log.debug("list all workflows for project {}", project);
        Specification<WorkflowEntity> specification = Specification.allOf(CommonSpecification.projectEquals(project));

        return entityService.searchAll(specification);
    }

    @Override
    public Page<Workflow> listWorkflowsByProject(@NotNull String project, Pageable pageable) {
        log.debug("list all workflows for project {}  page {}", project, pageable);
        Specification<WorkflowEntity> specification = Specification.allOf(CommonSpecification.projectEquals(project));

        return entityService.search(specification, pageable);
    }

    @Override
    public List<Workflow> listLatestWorkflowsByProject(@NotNull String project) {
        log.debug("list latest workflows for project {}", project);
        Specification<WorkflowEntity> specification = Specification.allOf(
            CommonSpecification.projectEquals(project),
            CommonSpecification.latestByProject(project)
        );

        return entityService.searchAll(specification);
    }

    @Override
    public Page<Workflow> listLatestWorkflowsByProject(@NotNull String project, Pageable pageable) {
        log.debug("list latest workflows for project {}  page {}", project, pageable);
        Specification<WorkflowEntity> specification = Specification.allOf(
            CommonSpecification.projectEquals(project),
            CommonSpecification.latestByProject(project)
        );

        return entityService.search(specification, pageable);
    }

    @Override
    public Page<Workflow> searchWorkflowsByProject(
        @NotNull String project,
        Pageable pageable,
        @Nullable SearchFilter<WorkflowEntity> filter
    ) {
        log.debug("search all workflows for project {} with {} page {}", project, String.valueOf(filter), pageable);
        Specification<WorkflowEntity> filterSpecification = filter != null ? filter.toSpecification() : null;
        Specification<WorkflowEntity> specification = Specification.allOf(
            CommonSpecification.projectEquals(project),
            filterSpecification
        );

        return entityService.search(specification, pageable);
    }

    @Override
    public Page<Workflow> searchLatestWorkflowsByProject(
        @NotNull String project,
        Pageable pageable,
        @Nullable SearchFilter<WorkflowEntity> filter
    ) {
        log.debug("search latest workflows for project {} with {} page {}", project, String.valueOf(filter), pageable);
        Specification<WorkflowEntity> filterSpecification = filter != null ? filter.toSpecification() : null;
        Specification<WorkflowEntity> specification = Specification.allOf(
            CommonSpecification.projectEquals(project),
            CommonSpecification.latestByProject(project),
            filterSpecification
        );

        return entityService.search(specification, pageable);
    }

    @Override
    public List<Workflow> findWorkflows(@NotNull String project, @NotNull String name) {
        log.debug("find workflows for project {} with name {}", project, name);

        //fetch all versions ordered by date DESC
        Specification<WorkflowEntity> where = Specification.allOf(
            CommonSpecification.projectEquals(project),
            CommonSpecification.nameEquals(name)
        );
        Specification<WorkflowEntity> specification = (root, query, builder) -> {
            query.orderBy(builder.desc(root.get(WorkflowEntity_.CREATED)));
            return where.toPredicate(root, query, builder);
        };

        return entityService.searchAll(specification);
    }

    @Override
    public Page<Workflow> findWorkflows(@NotNull String project, @NotNull String name, Pageable pageable) {
        log.debug("find workflows for project {} with name {} page {}", project, name, pageable);

        //fetch all versions ordered by date DESC
        Specification<WorkflowEntity> where = Specification.allOf(
            CommonSpecification.projectEquals(project),
            CommonSpecification.nameEquals(name)
        );
        Specification<WorkflowEntity> specification = (root, query, builder) -> {
            query.orderBy(builder.desc(root.get(WorkflowEntity_.CREATED)));
            return where.toPredicate(root, query, builder);
        };

        return entityService.search(specification, pageable);
    }

    @Override
    public Workflow findWorkflow(@NotNull String id) {
        log.debug("find workflow with id {}", String.valueOf(id));

        return entityService.find(id);
    }

    @Override
    public Workflow getWorkflow(@NotNull String id) throws NoSuchEntityException {
        log.debug("get workflow with id {}", String.valueOf(id));

        try {
            return entityService.get(id);
        } catch (NoSuchEntityException e) {
            throw new NoSuchEntityException(EntityName.ARTIFACT.toString());
        }
    }

    @Override
    public Workflow getLatestWorkflow(@NotNull String project, @NotNull String name) throws NoSuchEntityException {
        log.debug("get latest workflow for project {} with name {}", project, name);

        //fetch latest version ordered by date DESC
        Specification<WorkflowEntity> specification = CommonSpecification.latestByProject(project, name);
        return entityService.searchAll(specification).stream().findFirst().orElseThrow(NoSuchEntityException::new);
    }

    @Override
    public Workflow createWorkflow(@NotNull Workflow dto) throws DuplicatedEntityException {
        log.debug("create workflow");
        if (log.isTraceEnabled()) {
            log.trace("dto: {}", dto);
        }

        // Parse and export Spec
        Spec spec = specRegistry.createSpec(dto.getKind(), EntityName.WORKFLOW, dto.getSpec());
        if (spec == null) {
            throw new IllegalArgumentException("invalid kind");
        }

        //TODO validate

        //update spec as exported
        dto.setSpec(spec.toMap());

        try {
            return entityService.create(dto);
        } catch (DuplicatedEntityException e) {
            throw new DuplicatedEntityException(EntityName.ARTIFACT.toString(), dto.getId());
        }
    }

    @Override
    public Workflow updateWorkflow(@NotNull String id, @NotNull Workflow workflowDTO) throws NoSuchEntityException {
        log.debug("update workflow with id {}", String.valueOf(id));
        try {
            //fetch current and merge
            Workflow current = entityService.get(id);

            //spec is not modificable: enforce current
            workflowDTO.setSpec(current.getSpec());

            //update
            return entityService.update(id, workflowDTO);
        } catch (NoSuchEntityException e) {
            throw new NoSuchEntityException(EntityName.ARTIFACT.toString());
        }
    }

    @Override
    public void deleteWorkflow(@NotNull String id) {
        log.debug("delete workflow with id {}", String.valueOf(id));

        entityService.delete(id);
    }

    @Override
    public void deleteWorkflows(@NotNull String project, @NotNull String name) {
        log.debug("delete workflows for project {} with name {}", project, name);

        Specification<WorkflowEntity> spec = Specification.allOf(
            CommonSpecification.projectEquals(project),
            CommonSpecification.nameEquals(name)
        );

        long count = entityService.deleteAll(spec);
        log.debug("deleted count {}", count);
    }

    @Override
    public void deleteWorkflowsByProject(@NotNull String project) {
        log.debug("delete workflows for project {}", project);

        entityService.deleteAll(CommonSpecification.projectEquals(project));
    }
}
