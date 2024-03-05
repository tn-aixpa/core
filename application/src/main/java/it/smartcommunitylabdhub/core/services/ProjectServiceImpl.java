package it.smartcommunitylabdhub.core.services;

import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.models.entities.artifact.Artifact;
import it.smartcommunitylabdhub.commons.models.entities.dataitem.DataItem;
import it.smartcommunitylabdhub.commons.models.entities.function.Function;
import it.smartcommunitylabdhub.commons.models.entities.project.Project;
import it.smartcommunitylabdhub.commons.models.entities.workflow.Workflow;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.queries.SearchFilter;
import it.smartcommunitylabdhub.commons.services.entities.ArtifactService;
import it.smartcommunitylabdhub.commons.services.entities.DataItemService;
import it.smartcommunitylabdhub.commons.services.entities.FunctionService;
import it.smartcommunitylabdhub.commons.services.entities.SecretService;
import it.smartcommunitylabdhub.commons.services.entities.WorkflowService;
import it.smartcommunitylabdhub.commons.utils.EmbedUtils;
import it.smartcommunitylabdhub.core.models.entities.project.ProjectEntity;
import it.smartcommunitylabdhub.core.models.entities.project.specs.ProjectSpec;
import it.smartcommunitylabdhub.core.models.queries.services.SearchableProjectService;
import it.smartcommunitylabdhub.core.models.queries.specifications.CommonSpecification;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Transactional
@Slf4j
public class ProjectServiceImpl implements SearchableProjectService {

    @Autowired
    private EntityService<Project, ProjectEntity> entityService;

    @Autowired
    private FunctionService functionService;

    @Autowired
    private ArtifactService artifactService;

    @Autowired
    private DataItemService dataItemService;

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private SecretService secretService;

    @Override
    public Page<Project> listProjects(Pageable pageable) {
        log.debug("list projects page {}", pageable);

        return entityService.list(pageable);
    }

    @Override
    public Page<Project> searchProjects(Pageable pageable, @Nullable SearchFilter<ProjectEntity> filter) {
        log.debug("list projects page {}, filter {}", pageable, String.valueOf(filter));

        Specification<ProjectEntity> specification = filter != null ? filter.toSpecification() : null;
        if (specification != null) {
            return entityService.search(specification, pageable);
        } else {
            return entityService.list(pageable);
        }
    }

    @Override
    public Project findProjectByName(@NotNull String name) {
        log.debug("find project by name {}", name);

        return entityService.searchAll(CommonSpecification.nameEquals(name)).stream().findFirst().orElse(null);
    }

    @Override
    public Project findProject(@NotNull String id) {
        log.debug("find project with id {}", String.valueOf(id));

        return entityService.find(id);
    }

    @Override
    public Project getProject(@NotNull String id) throws NoSuchEntityException {
        log.debug("get project with id {}", String.valueOf(id));

        try {
            Project project = entityService.get(id);

            //load content
            log.debug("load project content for project {}", String.valueOf(id));

            List<Artifact> artifacts = artifactService.listLatestArtifactsByProject(id);
            List<DataItem> dataItems = dataItemService.listLatestDataItemsByProject(id);
            List<Function> functions = functionService.listLatestFunctionsByProject(id);
            List<Workflow> workflows = workflowService.listLatestWorkflowsByProject(id);

            //update spec
            ProjectSpec spec = new ProjectSpec();
            spec.configure(project.getSpec());

            //embed
            spec.setArtifacts(artifacts.stream().map(EmbedUtils::embed).collect(Collectors.toList()));
            spec.setDataitems(dataItems.stream().map(EmbedUtils::embed).collect(Collectors.toList()));
            spec.setFunctions(functions.stream().map(EmbedUtils::embed).collect(Collectors.toList()));
            spec.setWorkflows(workflows.stream().map(EmbedUtils::embed).collect(Collectors.toList()));

            project.setSpec(spec.toMap());

            return project;
        } catch (NoSuchEntityException e) {
            throw new NoSuchEntityException(EntityName.PROJECT.toString());
        }
    }

    @Override
    public Project getProjectByName(@NotNull String name) throws NoSuchEntityException {
        log.debug("get project by name {}", name);

        Project project = entityService
            .searchAll(CommonSpecification.nameEquals(name))
            .stream()
            .findFirst()
            .orElseThrow(() -> new NoSuchEntityException(EntityName.PROJECT.toString()));

        return getProject(project.getId());
    }

    @Override
    public Project createProject(@NotNull Project dto) throws DuplicatedEntityException {
        log.debug("create project");

        try {
            // Parse and export Spec
            ProjectSpec spec = new ProjectSpec();
            spec.configure(dto.getSpec());

            //TODO validate spec via validator
            //update spec as exported
            dto.setSpec(spec.toMap());

            //check if a project for this name already exists
            String name = dto.getName();
            if (!StringUtils.hasText(name)) {
                throw new IllegalArgumentException("invalid or missing name");
            }

            Project existing = findProjectByName(dto.getName());
            if (existing != null) {
                throw new DuplicatedEntityException(EntityName.PROJECT.toString(), name);
            }

            //create as new
            return entityService.create(dto);
        } catch (DuplicatedEntityException e) {
            throw new DuplicatedEntityException(EntityName.PROJECT.toString(), dto.getId());
        }
    }

    @Override
    public Project updateProject(@NotNull String id, @NotNull Project dto) throws NoSuchEntityException {
        log.debug("update project with id {}", String.valueOf(id));
        try {
            // Parse and export Spec
            ProjectSpec spec = new ProjectSpec();
            spec.configure(dto.getSpec());

            //TODO validate spec via validator
            //update spec as exported
            dto.setSpec(spec.toMap());

            //full update, project is modifiable
            return entityService.update(id, dto);
        } catch (NoSuchEntityException e) {
            throw new NoSuchEntityException(EntityName.PROJECT.toString());
        }
    }

    @Override
    public void deleteProject(@NotNull String id, @Nullable Boolean cascade) {
        log.debug("delete project with id {}", String.valueOf(id));

        Project prj = findProject(id);
        if (prj != null) {
            if (Boolean.TRUE.equals(cascade)) {
                String project = prj.getName();

                log.debug("cascade delete artifacts for project with id {}", String.valueOf(id));
                artifactService.deleteArtifactsByProject(project);

                log.debug("cascade delete dataItems for project with id {}", String.valueOf(id));
                dataItemService.deleteDataItemsByProject(project);

                log.debug("cascade delete functions for project with id {}", String.valueOf(id));
                functionService.deleteFunctionsByProject(project);

                log.debug("cascade delete workflows for project with id {}", String.valueOf(id));
                workflowService.deleteWorkflowsByProject(project);

                log.debug("cascade delete secrets for project with id {}", String.valueOf(id));
                secretService.deleteSecretsByProject(project);
            }

            //delete the project
            entityService.delete(id);
        }

        entityService.delete(id);
    }
}
