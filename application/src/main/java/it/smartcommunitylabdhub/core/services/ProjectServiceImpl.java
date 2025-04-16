package it.smartcommunitylabdhub.core.services;

import it.smartcommunitylabdhub.authorization.model.ResourceShareEntity;
import it.smartcommunitylabdhub.authorization.services.AuthorizableAwareEntityService;
import it.smartcommunitylabdhub.authorization.services.ResourceSharingService;
import it.smartcommunitylabdhub.authorization.services.ShareableAwareEntityService;
import it.smartcommunitylabdhub.commons.config.ApplicationProperties;
import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import it.smartcommunitylabdhub.commons.exceptions.SystemException;
import it.smartcommunitylabdhub.commons.models.artifact.Artifact;
import it.smartcommunitylabdhub.commons.models.base.BaseDTO;
import it.smartcommunitylabdhub.commons.models.dataitem.DataItem;
import it.smartcommunitylabdhub.commons.models.entities.EntityName;
import it.smartcommunitylabdhub.commons.models.function.Function;
import it.smartcommunitylabdhub.commons.models.metadata.EmbeddableMetadata;
import it.smartcommunitylabdhub.commons.models.metadata.MetadataDTO;
import it.smartcommunitylabdhub.commons.models.model.Model;
import it.smartcommunitylabdhub.commons.models.project.Project;
import it.smartcommunitylabdhub.commons.models.queries.SearchFilter;
import it.smartcommunitylabdhub.commons.models.relationships.RelationshipDetail;
import it.smartcommunitylabdhub.commons.models.workflow.Workflow;
import it.smartcommunitylabdhub.commons.services.ArtifactService;
import it.smartcommunitylabdhub.commons.services.DataItemService;
import it.smartcommunitylabdhub.commons.services.FunctionService;
import it.smartcommunitylabdhub.commons.services.LabelService;
import it.smartcommunitylabdhub.commons.services.ModelService;
import it.smartcommunitylabdhub.commons.services.RelationshipsAwareEntityService;
import it.smartcommunitylabdhub.commons.services.SecretService;
import it.smartcommunitylabdhub.commons.services.WorkflowService;
import it.smartcommunitylabdhub.commons.utils.EmbedUtils;
import it.smartcommunitylabdhub.commons.utils.MapUtils;
import it.smartcommunitylabdhub.core.components.infrastructure.specs.SpecValidator;
import it.smartcommunitylabdhub.core.models.entities.ProjectEntity;
import it.smartcommunitylabdhub.core.models.entities.RelationshipEntity;
import it.smartcommunitylabdhub.core.models.queries.services.SearchableProjectService;
import it.smartcommunitylabdhub.core.models.queries.specifications.CommonSpecification;
import it.smartcommunitylabdhub.core.models.specs.project.ProjectSpec;
import it.smartcommunitylabdhub.core.relationships.EntityRelationshipsService;
import it.smartcommunitylabdhub.core.utils.RefUtils;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;

@Service
@Transactional
@Slf4j
public class ProjectServiceImpl
    implements
        SearchableProjectService,
        AuthorizableAwareEntityService<Project>,
        ShareableAwareEntityService<Project>,
        RelationshipsAwareEntityService<Project> {

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private EntityService<Project, ProjectEntity> entityService;

    //TODO convert dependant services for cascade into callable (callback) functions
    @Autowired
    private FunctionService functionService;

    @Autowired
    private ArtifactService artifactService;

    @Autowired
    private DataItemService dataItemService;

    @Autowired
    private ModelService modelService;

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private SecretService secretService;

    @Autowired
    private LabelService labelService;

    @Autowired
    private ResourceSharingService sharingService;

    @Autowired
    private EntityRelationshipsService relationshipsService;

    @Autowired
    private SpecValidator validator;

    @Override
    public Page<Project> listProjects(Pageable pageable) {
        log.debug("list projects page {}", pageable);
        try {
            return entityService.list(pageable);
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public List<Project> listProjectsByUser(@NotNull String user) {
        log.debug("list all projects for user {}", user);
        try {
            return entityService.searchAll(CommonSpecification.createdByEquals(user));
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public Page<Project> searchProjects(Pageable pageable, @Nullable SearchFilter<ProjectEntity> filter) {
        log.debug("list projects page {}, filter {}", pageable, String.valueOf(filter));
        try {
            Specification<ProjectEntity> specification = filter != null ? filter.toSpecification() : null;
            if (specification != null) {
                return entityService.search(specification, pageable);
            } else {
                return entityService.list(pageable);
            }
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public Project findProjectByName(@NotNull String name) {
        log.debug("find project by name {}", name);
        try {
            return entityService.searchAll(CommonSpecification.nameEquals(name)).stream().findFirst().orElse(null);
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public Project findProject(@NotNull String id) {
        log.debug("find project with id {}", String.valueOf(id));
        try {
            return entityService.find(id);
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
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
            List<Model> models = modelService.listLatestModelsByProject(id);
            List<Function> functions = functionService.listLatestFunctionsByProject(id);
            List<Workflow> workflows = workflowService.listLatestWorkflowsByProject(id);

            //update spec
            ProjectSpec spec = new ProjectSpec();
            spec.configure(project.getSpec());

            //embed + ref
            spec.setArtifacts(
                artifacts.stream().map(EmbedUtils::embed).map(this::inlineRef).collect(Collectors.toList())
            );
            spec.setDataitems(
                dataItems.stream().map(EmbedUtils::embed).map(this::inlineRef).collect(Collectors.toList())
            );
            spec.setModels(models.stream().map(EmbedUtils::embed).map(this::inlineRef).collect(Collectors.toList()));
            spec.setFunctions(
                functions.stream().map(EmbedUtils::embed).map(this::inlineRef).collect(Collectors.toList())
            );
            spec.setWorkflows(
                workflows.stream().map(EmbedUtils::embed).map(this::inlineRef).collect(Collectors.toList())
            );

            project.setSpec(spec.toMap());

            return project;
        } catch (NoSuchEntityException e) {
            throw new NoSuchEntityException(EntityName.PROJECT.toString());
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public Project getProjectByName(@NotNull String name) throws NoSuchEntityException {
        log.debug("get project by name {}", name);
        try {
            Project project = entityService
                .searchAll(CommonSpecification.nameEquals(name))
                .stream()
                .findFirst()
                .orElseThrow(() -> new NoSuchEntityException(EntityName.PROJECT.toString()));

            return getProject(project.getId());
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    @CacheEvict(
        value = {
            "findIdByCreatedBy",
            "findIdByUpdatedBy",
            "findIdByProject",
            "findNameByCreatedBy",
            "findNameByUpdatedBy",
            "findNameByProject",
        },
        allEntries = true
    )
    public Project createProject(@NotNull Project dto)
        throws DuplicatedEntityException, BindException, IllegalArgumentException {
        log.debug("create project");

        try {
            // Parse and export Spec
            ProjectSpec spec = new ProjectSpec();
            spec.configure(dto.getSpec());

            //TODO build a default config?

            //validate
            validator.validateSpec(spec);

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

            //make sure project id matches name (for slugs)
            if (StringUtils.hasText(dto.getId()) && !dto.getName().equals(dto.getId())) {
                throw new IllegalArgumentException("project id must match name");
            }

            //enforce
            dto.setId(name);

            //create as new
            return entityService.create(dto);
        } catch (DuplicatedEntityException e) {
            throw new DuplicatedEntityException(EntityName.PROJECT.toString(), dto.getId());
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public Project updateProject(@NotNull String id, @NotNull Project dto)
        throws NoSuchEntityException, BindException, IllegalArgumentException {
        log.debug("update project with id {}", String.valueOf(id));
        try {
            // Parse and export Spec
            ProjectSpec spec = new ProjectSpec();
            spec.configure(dto.getSpec());

            //validate
            validator.validateSpec(spec);

            //update spec as exported
            dto.setSpec(spec.toMap());

            //full update, project is modifiable
            return entityService.update(id, dto);
        } catch (NoSuchEntityException e) {
            throw new NoSuchEntityException(EntityName.PROJECT.toString());
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    @CacheEvict(
        value = {
            "findIdByCreatedBy",
            "findIdByUpdatedBy",
            "findIdByProject",
            "findNameByCreatedBy",
            "findNameByUpdatedBy",
            "findNameByProject",
            "findIdsBySharedTo",
            "findNamesBySharedTo",
        },
        allEntries = true
    )
    public void deleteProject(@NotNull String id, @Nullable Boolean cascade) {
        log.debug("delete project with id {}", String.valueOf(id));
        try {
            Project prj = entityService.find(id);
            if (prj != null) {
                if (Boolean.TRUE.equals(cascade)) {
                    String project = prj.getName();

                    log.debug("cascade delete artifacts for project with id {}", String.valueOf(id));
                    artifactService.deleteArtifactsByProject(project, Boolean.TRUE);

                    log.debug("cascade delete dataItems for project with id {}", String.valueOf(id));
                    dataItemService.deleteDataItemsByProject(project, Boolean.TRUE);

                    log.debug("cascade delete models for project with id {}", String.valueOf(id));
                    modelService.deleteModelsByProject(project, Boolean.TRUE);

                    log.debug("cascade delete functions for project with id {}", String.valueOf(id));
                    functionService.deleteFunctionsByProject(project);

                    log.debug("cascade delete workflows for project with id {}", String.valueOf(id));
                    workflowService.deleteWorkflowsByProject(project);

                    log.debug("cascade delete secrets for project with id {}", String.valueOf(id));
                    secretService.deleteSecretsByProject(project);

                    log.debug("cascade delete labels for project with id {}", String.valueOf(id));
                    labelService.deleteLabelsByProject(project);
                }

                //delete the project
                entityService.delete(id);
            }
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    @Cacheable("findIdByCreatedBy")
    public List<String> findIdsByCreatedBy(@NotNull String createdBy) {
        log.debug("find id of projects for createdBy {}", createdBy);
        try {
            return entityService
                .searchAll(CommonSpecification.createdByEquals(createdBy))
                .stream()
                .map(p -> p.getId())
                .toList();
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    @Cacheable("findIdByUpdatedBy")
    public List<String> findIdsByUpdatedBy(@NotNull String updatedBy) {
        log.debug("find id of projects for updatedBy {}", updatedBy);
        try {
            return entityService
                .searchAll(CommonSpecification.updatedByEquals(updatedBy))
                .stream()
                .map(p -> p.getId())
                .toList();
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    @Cacheable("findIdByProject")
    public List<String> findIdsByProject(@NotNull String project) {
        log.debug("find id of projects for project {}", project);
        try {
            Project p = entityService.find(project);
            if (p == null) {
                return Collections.emptyList();
            }

            return Collections.singletonList(p.getId());
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    @Cacheable("findNameByCreatedBy")
    public List<String> findNamesByCreatedBy(@NotNull String createdBy) {
        log.debug("find name of projects for createdBy {}", createdBy);
        try {
            return entityService
                .searchAll(CommonSpecification.createdByEquals(createdBy))
                .stream()
                .map(p -> p.getName())
                .toList();
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    @Cacheable("findNameByUpdatedBy")
    public List<String> findNamesByUpdatedBy(@NotNull String updatedBy) {
        log.debug("find name of projects for updatedBy {}", updatedBy);
        try {
            return entityService
                .searchAll(CommonSpecification.updatedByEquals(updatedBy))
                .stream()
                .map(p -> p.getName())
                .toList();
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    @Cacheable("findNameByProject")
    public List<String> findNamesByProject(@NotNull String project) {
        log.debug("find name of projects for project {}", project);
        try {
            Project p = entityService.find(project);
            if (p == null) {
                return Collections.emptyList();
            }

            return Collections.singletonList(p.getName());
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    @Cacheable("findIdsBySharedTo")
    public List<String> findIdsBySharedTo(@NotNull String user) {
        log.debug("find ids of projects shared to {}", user);
        try {
            List<ResourceShareEntity> shares = sharingService
                .listByUser(user)
                .stream()
                .filter(s -> EntityName.PROJECT.getValue().equals(s.getEntity()))
                .toList();

            //for every project check if owner matches
            //DISABLED, we expect shares to be valid
            return shares
                .stream()
                .map(s -> {
                    try {
                        Project p = entityService.find(s.getEntityId());
                        // if (p != null && p.getUser() != null && p.getUser().equals(s.getOwner())) {
                        return p;
                        // }
                    } catch (StoreException e) {
                        log.error("store error: {}", e.getMessage());
                    }

                    return null;
                })
                .filter(p -> p != null)
                .map(p -> p.getId())
                .toList();
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    @Cacheable("findNamesBySharedTo")
    public List<String> findNamesBySharedTo(@NotNull String user) {
        log.debug("find name of projects shared to {}", user);
        try {
            List<ResourceShareEntity> shares = sharingService
                .listByUser(user)
                .stream()
                .filter(s -> EntityName.PROJECT.getValue().equals(s.getEntity()))
                .toList();

            //for every project check if owner matches
            //DISABLED, we expect shares to be valid
            return shares
                .stream()
                .map(s -> {
                    try {
                        Project p = entityService.find(s.getEntityId());
                        // if (p != null && p.getUser() != null && p.getUser().equals(s.getOwner())) {
                        return p;
                        // }
                    } catch (StoreException e) {
                        log.error("store error: {}", e.getMessage());
                    }

                    return null;
                })
                .filter(p -> p != null)
                .map(p -> p.getName())
                .toList();
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    @CacheEvict(value = { "findIdsBySharedTo", "findNamesBySharedTo" }, allEntries = true)
    public ResourceShareEntity share(@NotNull String id, @NotNull String user) {
        log.debug("share project with id {} to {}", String.valueOf(id), String.valueOf(user));

        try {
            Project project = entityService.get(id);

            //check if a share with same user already exists
            List<ResourceShareEntity> shares = sharingService.listByProjectAndEntity(
                project.getProject(),
                EntityName.PROJECT,
                id,
                user
            );
            if (!shares.isEmpty()) {
                return shares.get(0);
            }

            //create
            ResourceShareEntity share = sharingService.share(project.getProject(), EntityName.PROJECT, id, user);

            if (log.isTraceEnabled()) {
                log.trace("share: {}", share);
            }

            return share;
        } catch (NoSuchEntityException e) {
            throw new NoSuchEntityException(EntityName.PROJECT.toString());
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    @CacheEvict(value = { "findIdsBySharedTo", "findNamesBySharedTo" }, allEntries = true)
    public void revoke(@NotNull String id, @NotNull String shareId) {
        log.debug("revoke share project {} with id {}", String.valueOf(id), String.valueOf(shareId));

        try {
            Project project = entityService.get(id);
            ResourceShareEntity share = sharingService.get(shareId);

            if (share == null) {
                return;
            }

            //check project match
            if (!project.getId().equals(share.getProject())) {
                throw new IllegalArgumentException("project-mismatch");
            }
            if (!id.equals(share.getEntityId()) || !EntityName.PROJECT.getValue().equals(share.getEntity())) {
                throw new IllegalArgumentException("invalid");
            }

            //revoke
            sharingService.revoke(shareId);
        } catch (NoSuchEntityException e) {
            throw new NoSuchEntityException(EntityName.PROJECT.toString());
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public List<ResourceShareEntity> listSharesById(@NotNull String id) {
        log.debug("list shares for project with id {}", String.valueOf(id));
        try {
            return sharingService.listByProjectAndEntity(id, EntityName.PROJECT, id);
        } catch (NoSuchEntityException e) {
            throw new NoSuchEntityException(EntityName.PROJECT.toString());
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public List<RelationshipDetail> getRelationships(@NotNull String id) {
        log.debug("list relationships for project with id {}", String.valueOf(id));
        try {
            //fully load project
            Project project = getProject(id);
            ProjectSpec spec = new ProjectSpec();
            spec.configure(project.getSpec());

            //load all relationships
            List<RelationshipEntity> list = relationshipsService.listByProject(id);

            //filter for elements embedded in project
            List<String> ids = new ArrayList<>();
            Optional.ofNullable(spec.getArtifacts()).ifPresent(artifacts -> artifacts.forEach(a -> ids.add(a.getId())));
            Optional.ofNullable(spec.getDataitems()).ifPresent(dataItems -> dataItems.forEach(d -> ids.add(d.getId())));
            Optional.ofNullable(spec.getFunctions()).ifPresent(functions -> functions.forEach(f -> ids.add(f.getId())));
            Optional.ofNullable(spec.getModels()).ifPresent(models -> models.forEach(m -> ids.add(m.getId())));
            Optional.ofNullable(spec.getWorkflows()).ifPresent(workflows -> workflows.forEach(w -> ids.add(w.getId())));

            return list
                .stream()
                .filter(e -> ids.contains(e.getSourceId()) || ids.contains(e.getDestId()))
                .map(e -> new RelationshipDetail(e.getRelationship(), e.getSourceKey(), e.getDestKey()))
                .toList();
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    //TODO add to all services
    private <T extends BaseDTO & MetadataDTO> T inlineRef(T d) {
        String applicationUrl = applicationProperties.getEndpoint();
        String api = applicationProperties.getApi();

        if (applicationUrl != null && api != null) {
            String ref = RefUtils.getRefPath(d);
            if (ref != null) {
                EmbeddableMetadata em = EmbeddableMetadata.from(d.getMetadata());
                em.setRef(applicationUrl + "/api/" + api + ref);
                d.setMetadata(MapUtils.mergeMultipleMaps(d.getMetadata(), em.toMap()));
            }
        }

        return d;
    }
}
