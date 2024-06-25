package it.smartcommunitylabdhub.core.services;

import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import it.smartcommunitylabdhub.commons.exceptions.SystemException;
import it.smartcommunitylabdhub.commons.models.base.DownloadInfo;
import it.smartcommunitylabdhub.commons.models.base.FileInfo;
import it.smartcommunitylabdhub.commons.models.base.UploadInfo;
import it.smartcommunitylabdhub.commons.models.entities.artifact.Artifact;
import it.smartcommunitylabdhub.commons.models.entities.artifact.ArtifactBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.project.Project;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.queries.SearchFilter;
import it.smartcommunitylabdhub.commons.models.specs.Spec;
import it.smartcommunitylabdhub.commons.services.SpecRegistry;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecValidator;
import it.smartcommunitylabdhub.core.models.builders.artifact.ArtifactEntityBuilder;
import it.smartcommunitylabdhub.core.models.entities.AbstractEntity_;
import it.smartcommunitylabdhub.core.models.entities.ArtifactEntity;
import it.smartcommunitylabdhub.core.models.entities.ProjectEntity;
import it.smartcommunitylabdhub.core.models.files.ArtifactFilesService;
import it.smartcommunitylabdhub.core.models.indexers.ArtifactEntityIndexer;
import it.smartcommunitylabdhub.core.models.indexers.IndexableArtifactService;
import it.smartcommunitylabdhub.core.models.queries.services.SearchableArtifactService;
import it.smartcommunitylabdhub.core.models.queries.specifications.CommonSpecification;
import it.smartcommunitylabdhub.files.service.FilesService;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;

@Service
@Transactional
@Slf4j
public class ArtifactServiceImpl implements SearchableArtifactService, IndexableArtifactService, ArtifactFilesService {

    @Autowired
    private EntityService<Artifact, ArtifactEntity> entityService;

    @Autowired
    private EntityService<Project, ProjectEntity> projectService;

    @Autowired
    private ArtifactEntityIndexer indexer;

    @Autowired
    private ArtifactEntityBuilder entityBuilder;

    @Autowired
    private SpecRegistry specRegistry;

    @Autowired
    private SpecValidator validator;

    @Autowired
    private FilesService filesService;

    @Override
    public Page<Artifact> listArtifacts(Pageable pageable) {
        log.debug("list artifacts page {}", pageable);

        try {
            return entityService.list(pageable);
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public List<Artifact> listLatestArtifacts() {
        log.debug("list latest artifacts");
        Specification<ArtifactEntity> specification = CommonSpecification.latest();

        try {
            return entityService.searchAll(specification);
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public Page<Artifact> listLatestArtifacts(Pageable pageable) {
        log.debug("list latest artifacts page {}", pageable);
        Specification<ArtifactEntity> specification = CommonSpecification.latest();
        try {
            return entityService.search(specification, pageable);
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public List<Artifact> listArtifactsByUser(@NotNull String user) {
        log.debug("list all artifacts for user {}  ", user);

        try {
            return entityService.searchAll(CommonSpecification.createdByEquals(user));
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public Page<Artifact> searchArtifacts(Pageable pageable, @Nullable SearchFilter<ArtifactEntity> filter) {
        log.debug("search artifacts page {}, filter {}", pageable, String.valueOf(filter));

        try {
            Specification<ArtifactEntity> specification = filter != null ? filter.toSpecification() : null;
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
    public Page<Artifact> searchLatestArtifacts(Pageable pageable, @Nullable SearchFilter<ArtifactEntity> filter) {
        log.debug("search latest artifacts with {} page {}", String.valueOf(filter), pageable);
        Specification<ArtifactEntity> filterSpecification = filter != null ? filter.toSpecification() : null;
        Specification<ArtifactEntity> specification = Specification.allOf(
            CommonSpecification.latest(),
            filterSpecification
        );
        try {
            return entityService.search(specification, pageable);
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public List<Artifact> listArtifactsByProject(@NotNull String project) {
        log.debug("list all artifacts for project {}  ", project);
        Specification<ArtifactEntity> specification = Specification.allOf(CommonSpecification.projectEquals(project));

        try {
            return entityService.searchAll(specification);
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public Page<Artifact> listArtifactsByProject(@NotNull String project, Pageable pageable) {
        log.debug("list all artifacts for project {}  page {}", project, pageable);
        Specification<ArtifactEntity> specification = Specification.allOf(CommonSpecification.projectEquals(project));
        try {
            return entityService.search(specification, pageable);
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public List<Artifact> listLatestArtifactsByProject(@NotNull String project) {
        log.debug("list artifacts for project {}  ", project);
        Specification<ArtifactEntity> specification = Specification.allOf(
            CommonSpecification.projectEquals(project),
            CommonSpecification.latestByProject(project)
        );
        try {
            return entityService.searchAll(specification);
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public Page<Artifact> listLatestArtifactsByProject(@NotNull String project, Pageable pageable) {
        log.debug("list artifacts for project {}  page {}", project, pageable);
        Specification<ArtifactEntity> specification = Specification.allOf(
            CommonSpecification.projectEquals(project),
            CommonSpecification.latestByProject(project)
        );
        try {
            return entityService.search(specification, pageable);
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public Page<Artifact> searchArtifactsByProject(
        @NotNull String project,
        Pageable pageable,
        @Nullable SearchFilter<ArtifactEntity> filter
    ) {
        log.debug("search all artifacts for project {} with {} page {}", project, String.valueOf(filter), pageable);
        Specification<ArtifactEntity> filterSpecification = filter != null ? filter.toSpecification() : null;
        Specification<ArtifactEntity> specification = Specification.allOf(
            CommonSpecification.projectEquals(project),
            filterSpecification
        );
        try {
            return entityService.search(specification, pageable);
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public Page<Artifact> searchLatestArtifactsByProject(
        @NotNull String project,
        Pageable pageable,
        @Nullable SearchFilter<ArtifactEntity> filter
    ) {
        log.debug("search latest artifacts for project {} with {} page {}", project, String.valueOf(filter), pageable);
        Specification<ArtifactEntity> filterSpecification = filter != null ? filter.toSpecification() : null;
        Specification<ArtifactEntity> specification = Specification.allOf(
            CommonSpecification.projectEquals(project),
            CommonSpecification.latestByProject(project),
            filterSpecification
        );
        try {
            return entityService.search(specification, pageable);
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
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
            query.orderBy(builder.desc(root.get(AbstractEntity_.CREATED)));
            return where.toPredicate(root, query, builder);
        };
        try {
            return entityService.searchAll(specification);
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
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
            query.orderBy(builder.desc(root.get(AbstractEntity_.CREATED)));
            return where.toPredicate(root, query, builder);
        };
        try {
            return entityService.search(specification, pageable);
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public Artifact findArtifact(@NotNull String id) {
        log.debug("find artifact with id {}", String.valueOf(id));
        try {
            return entityService.find(id);
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public Artifact getArtifact(@NotNull String id) throws NoSuchEntityException {
        log.debug("get artifact with id {}", String.valueOf(id));

        try {
            return entityService.get(id);
        } catch (NoSuchEntityException e) {
            throw new NoSuchEntityException(EntityName.ARTIFACT.toString());
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public Artifact getLatestArtifact(@NotNull String project, @NotNull String name) throws NoSuchEntityException {
        log.debug("get latest artifact for project {} with name {}", project, name);

        //fetch latest version ordered by date DESC
        Specification<ArtifactEntity> specification = CommonSpecification.latestByProject(project, name);
        try {
            return entityService.searchAll(specification).stream().findFirst().orElseThrow(NoSuchEntityException::new);
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public Artifact createArtifact(@NotNull Artifact dto)
        throws DuplicatedEntityException, BindException, IllegalArgumentException {
        log.debug("create artifact");
        if (log.isTraceEnabled()) {
            log.trace("dto: {}", dto);
        }
        try {
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

            //validate
            validator.validateSpec(spec);

            //update spec as exported
            dto.setSpec(spec.toMap());

            try {
                if (log.isTraceEnabled()) {
                    log.trace("storable dto: {}", dto);
                }

                return entityService.create(dto);
            } catch (DuplicatedEntityException e) {
                throw new DuplicatedEntityException(EntityName.ARTIFACT.toString(), dto.getId());
            }
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
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
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public void deleteArtifact(@NotNull String id) {
        log.debug("delete artifact with id {}", String.valueOf(id));
        try {
            entityService.delete(id);
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public void deleteArtifacts(@NotNull String project, @NotNull String name) {
        log.debug("delete artifacts for project {} with name {}", project, name);

        Specification<ArtifactEntity> spec = Specification.allOf(
            CommonSpecification.projectEquals(project),
            CommonSpecification.nameEquals(name)
        );
        try {
            long count = entityService.deleteAll(spec);
            log.debug("deleted count {}", count);
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public void deleteArtifactsByProject(@NotNull String project) {
        log.debug("delete artifacts for project {}", project);
        try {
            entityService.deleteAll(CommonSpecification.projectEquals(project));
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public void indexArtifact(@NotNull String id) {
        log.debug("index artifact with id {}", String.valueOf(id));
        try {
            Artifact artifact = entityService.get(id);
            indexer.index(entityBuilder.convert(artifact));
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public void reindexArtifacts() {
        log.debug("reindex all artifacts");

        //clear index
        indexer.clearIndex();

        //use pagination and batch
        boolean hasMore = true;
        int pageNumber = 0;
        while (hasMore) {
            hasMore = false;

            try {
                Page<Artifact> page = entityService.list(
                    PageRequest.of(pageNumber, BaseEntityServiceImpl.PAGE_MAX_SIZE)
                );
                indexer.indexAll(
                    page.getContent().stream().map(e -> entityBuilder.convert(e)).collect(Collectors.toList())
                );
                hasMore = page.hasNext();
            } catch (IllegalArgumentException | StoreException | SystemException e) {
                hasMore = false;

                log.error("error with indexing: {}", e.getMessage());
            }
        }
    }

    @Override
    public DownloadInfo downloadAsUrl(@NotNull String id) throws NoSuchEntityException, SystemException {
        log.debug("download url for artifact with id {}", String.valueOf(id));

        try {
            Artifact artifact = entityService.get(id);

            //extract path from spec
            ArtifactBaseSpec spec = new ArtifactBaseSpec();
            spec.configure(artifact.getSpec());

            String path = spec.getPath();
            if (!StringUtils.hasText(path)) {
                throw new NoSuchEntityException("file");
            }

            DownloadInfo info = filesService.getDownloadAsUrl(path);
            if (log.isTraceEnabled()) {
                log.trace("download url for artifact with id {}: {} -> {}", id, path, info);
            }

            return info;
        } catch (NoSuchEntityException e) {
            throw new NoSuchEntityException(EntityName.ARTIFACT.toString());
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public List<FileInfo> getObjectMetadata(@NotNull String id) throws NoSuchEntityException, SystemException {
        log.debug("get storage metadata for artifact with id {}", String.valueOf(id));
        try {
            Artifact artifact = entityService.get(id);

            //extract path from spec
            ArtifactBaseSpec spec = new ArtifactBaseSpec();
            spec.configure(artifact.getSpec());

            String path = spec.getPath();
            if (!StringUtils.hasText(path)) {
                throw new NoSuchEntityException("file");
            }

            List<FileInfo> metadata = filesService.getObjectMetadata(path);
            if (log.isTraceEnabled()) {
                log.trace("metadata for artifact with id {}: {} -> {}", id, path, metadata);
            }

            return metadata;
        } catch (NoSuchEntityException e) {
            throw new NoSuchEntityException(EntityName.ARTIFACT.toString());
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

	@Override
	public UploadInfo uploadAsUrl(@NotNull String projectId, @NotNull String id, @NotNull String filename) 
			throws NoSuchEntityException, SystemException {
        log.debug("upload url for artifact with id {}", String.valueOf(id));

        UploadInfo info = filesService.getUploadAsUrl(EntityName.ARTIFACT.getValue(), projectId, id, filename);
        if (log.isTraceEnabled()) {
            log.trace("upload url for artifact with id {}: {}", id, info);
        }

        return info;
	}
	
	@Override
	public UploadInfo startUpload(@NotNull String projectId, @NotNull String id, @NotNull String filename) 
			throws NoSuchEntityException, SystemException {
        log.debug("start upload url for artifact with id {}", String.valueOf(id));

        UploadInfo info = filesService.startUpload(EntityName.ARTIFACT.getValue(), projectId, id, filename);
        if (log.isTraceEnabled()) {
            log.trace("start upload url for artifact with id {}: {}", id, info);
        }

        return info;		
	}
	
	@Override
	public UploadInfo uploadPart(@NotNull String path, @NotNull String uploadId, @NotNull Integer partNumber) 
			throws NoSuchEntityException, SystemException {
        log.debug("start upload url for artifact with path {}", path);

        UploadInfo info = filesService.uploadPart(path, uploadId, partNumber);
        if (log.isTraceEnabled()) {
            log.trace("part upload url for artifact with path {}: {}", path, info);
        }

        return info;
	}
	
	@Override
	public UploadInfo completeUpload(@NotNull String path, @NotNull String uploadId, @NotNull List<String> eTagPartList) 
			throws NoSuchEntityException, SystemException {
        log.debug("complete upload url for artifact with path {}", path);
        
        UploadInfo info = filesService.completeUpload(path, uploadId, eTagPartList);
        if (log.isTraceEnabled()) {
            log.trace("complete upload url for artifact with path {}: {}", path, info);
        }

        return info;
	}
}
