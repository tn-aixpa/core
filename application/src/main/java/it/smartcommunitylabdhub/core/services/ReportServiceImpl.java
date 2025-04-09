package it.smartcommunitylabdhub.core.services;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;

import it.smartcommunitylabdhub.commons.accessors.fields.StatusFieldAccessor;
import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import it.smartcommunitylabdhub.commons.exceptions.SystemException;
import it.smartcommunitylabdhub.commons.models.artifact.Artifact;
import it.smartcommunitylabdhub.commons.models.base.BaseDTO;
import it.smartcommunitylabdhub.commons.models.dataitem.DataItem;
import it.smartcommunitylabdhub.commons.models.entities.EntityName;
import it.smartcommunitylabdhub.commons.models.files.FileInfo;
import it.smartcommunitylabdhub.commons.models.files.FilesInfo;
import it.smartcommunitylabdhub.commons.models.function.Function;
import it.smartcommunitylabdhub.commons.models.model.Model;
import it.smartcommunitylabdhub.commons.models.project.Project;
import it.smartcommunitylabdhub.commons.models.queries.SearchFilter;
import it.smartcommunitylabdhub.commons.models.report.Report;
import it.smartcommunitylabdhub.commons.models.report.ReportBaseSpec;
import it.smartcommunitylabdhub.commons.models.workflow.Workflow;
import it.smartcommunitylabdhub.commons.services.FilesInfoService;
import it.smartcommunitylabdhub.core.models.base.BaseEntity;
import it.smartcommunitylabdhub.core.models.entities.AbstractEntity_;
import it.smartcommunitylabdhub.core.models.entities.ArtifactEntity;
import it.smartcommunitylabdhub.core.models.entities.DataItemEntity;
import it.smartcommunitylabdhub.core.models.entities.FunctionEntity;
import it.smartcommunitylabdhub.core.models.entities.ModelEntity;
import it.smartcommunitylabdhub.core.models.entities.ProjectEntity;
import it.smartcommunitylabdhub.core.models.entities.ReportEntity;
import it.smartcommunitylabdhub.core.models.entities.WorkflowEntity;
import it.smartcommunitylabdhub.core.models.queries.services.SearchableReportService;
import it.smartcommunitylabdhub.core.models.queries.specifications.CommonSpecification;
import it.smartcommunitylabdhub.files.models.DownloadInfo;
import it.smartcommunitylabdhub.files.models.UploadInfo;
import it.smartcommunitylabdhub.files.service.EntityFilesService;
import it.smartcommunitylabdhub.files.service.FilesService;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class ReportServiceImpl implements SearchableReportService, EntityFilesService<Report> {

    @Autowired
    private EntityService<Report, ReportEntity> entityService;

    @Autowired
    private EntityService<Model, ModelEntity> modelEntityService;
    @Autowired
    private EntityService<DataItem, DataItemEntity> dataItemEntityService;
    @Autowired
    private EntityService<Artifact, ArtifactEntity> artifactEntityService;
    @Autowired
    private EntityService<Function, FunctionEntity> functionEntityService;
    @Autowired
    private EntityService<Workflow, WorkflowEntity> workflowEntityService;

    @Autowired
    private EntityService<Project, ProjectEntity> projectService;

    @Autowired
    private FilesService filesService;

    @Autowired
    private FilesInfoService filesInfoService;

    @Override
    public Page<Report> listReports(Pageable pageable) throws SystemException {
        log.debug("list reports page {}", pageable);
        try {
            return entityService.list(pageable);
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    
    @Override
    public List<Report> listLatestReports() throws SystemException {
        log.debug("list latest reports");
        Specification<ReportEntity> specification = CommonSpecification.latest();

        try {
            return entityService.searchAll(specification);
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public Page<Report> listLatestReports(Pageable pageable) throws SystemException {
        log.debug("list latest reports page {}", pageable);
        Specification<ReportEntity> specification = CommonSpecification.latest();
        try {
            return entityService.search(specification, pageable);
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }

    }

    @Override
    public List<Report> listReportsByUser(@NotNull String user) throws SystemException {
        log.debug("list all reports for user {}  ", user);
        try {
            return entityService.searchAll(CommonSpecification.createdByEquals(user));
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public Page<Report> searchReports(Pageable pageable, @Nullable SearchFilter<ReportEntity> filter) throws SystemException {
        log.debug("list reports page {}, filter {}", pageable, String.valueOf(filter));
        try {
            Specification<ReportEntity> specification = filter != null ? filter.toSpecification() : null;
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
    public Page<Report> searchLatestReports(Pageable pageable, SearchFilter<ReportEntity> filter)
            throws SystemException {
        log.debug("search latest reports with {} page {}", String.valueOf(filter), pageable);
        Specification<ReportEntity> filterSpecification = filter != null ? filter.toSpecification() : null;
        Specification<ReportEntity> specification = Specification.allOf(
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
    public List<Report> listReportsByProject(@NotNull String project) throws SystemException {
        log.debug("list all reports for project {}  ", project);
        try {
            return entityService.searchAll(CommonSpecification.projectEquals(project));
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }


    @Override
    public Page<Report> listReportsByProject(@NotNull String project, Pageable pageable) throws SystemException {
        log.debug("list reports for project {} page {}", project, pageable);
        Specification<ReportEntity> specification = Specification.allOf(CommonSpecification.projectEquals(project));
        try {
            return entityService.search(specification, pageable);
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public List<Report> listLatestReportsByProject(@NotNull String project) throws SystemException {
        log.debug("list reports for project {}  ", project);
        Specification<ReportEntity> specification = Specification.allOf(
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
    public Page<Report> listLatestReportByProject(@NotNull String project, Pageable pageable) throws SystemException {
        log.debug("list reports for project {}  ", project);
        Specification<ReportEntity> specification = Specification.allOf(
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
    public Page<Report> searchReportsByProject(
        @NotNull String project, 
        Pageable pageable,
        @Nullable SearchFilter<ReportEntity> filter
    ) throws SystemException {
        log.debug("list reports for project {} with {} page {}", project, String.valueOf(filter), pageable);
        Specification<ReportEntity> filterSpecification = filter != null ? filter.toSpecification() : null;
        Specification<ReportEntity> specification = Specification.allOf(
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
    public Page<Report> searchLatestReportsByProject(@NotNull String project, Pageable pageable,
            SearchFilter<ReportEntity> filter) throws SystemException {
        log.debug("search latest reports for project {} with {} page {}", project, String.valueOf(filter), pageable);
        Specification<ReportEntity> filterSpecification = filter != null ? filter.toSpecification() : null;
        Specification<ReportEntity> specification = Specification.allOf(
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
    public List<Report> findReports(@NotNull String project, @NotNull String name) throws SystemException {
        log.debug("find reports for project {} with name {}", project, name);

        //fetch all versions ordered by date DESC
        Specification<ReportEntity> where = Specification.allOf(
            CommonSpecification.projectEquals(project),
            CommonSpecification.nameEquals(name)
        );
        Specification<ReportEntity> specification = (root, query, builder) -> {
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
    public Page<Report> findReports(@NotNull String project, @NotNull String name, Pageable pageable)
            throws SystemException {
        log.debug("find reports for project {} with name {} page {}", project, name, pageable);

        //fetch all versions ordered by date DESC
        Specification<ReportEntity> where = Specification.allOf(
            CommonSpecification.projectEquals(project),
            CommonSpecification.nameEquals(name)
        );
        Specification<ReportEntity> specification = (root, query, builder) -> {
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
    public Report findReport(@NotNull String id) throws SystemException {
        log.debug("find report with id {}", String.valueOf(id));
        try {
            return entityService.find(id);
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public Report getReport(@NotNull String id) throws NoSuchEntityException, SystemException {
        log.debug("get report with id {}", String.valueOf(id));

        try {
            return entityService.get(id);
        } catch (NoSuchEntityException e) {
            throw new NoSuchEntityException(EntityName.LOG.toString());
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }


    @Override
    public Report getLatestReport(@NotNull String project, @NotNull String name)
            throws NoSuchEntityException, SystemException {
        log.debug("get latest report for project {} with name {}", project, name);

        //fetch latest version ordered by date DESC
        Specification<ReportEntity> specification = CommonSpecification.latestByProject(project, name);
        try {
            return entityService.searchAll(specification).stream().findFirst().orElseThrow(NoSuchEntityException::new);
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }    
    }



    @Override
    public List<Report> getLatestReportsByEntity(@NotNull String entity, @NotNull String entityType)
            throws SystemException {
        log.debug("list reports for entity {}", entity);
        EntityService<?, ?> eService = getEntityService(entityType);
        try {
            BaseEntity e = (BaseEntity)eService.find(entity);
            if (e == null) {
                return Collections.emptyList();
            }

            //define a spec for reports building entity path
            Specification<ReportEntity> where = Specification.allOf(
                CommonSpecification.latestByProject(e.getProject()),
                createEntitySpecification(entity)
            );

            //fetch all reports ordered by date ASC
            Specification<ReportEntity> specification = (root, query, builder) -> {
                query.orderBy(builder.asc(root.get(AbstractEntity_.CREATED)));
                return where.toPredicate(root, query, builder);
            };

            return entityService.searchAll(specification);
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public List<Report> getReportsByEntity(@NotNull String entity, @NotNull String entityType) throws SystemException {
        log.debug("list reports for entity {}", entity);
        EntityService<?, ?> eService = getEntityService(entityType);
        try {
            BaseEntity e = (BaseEntity)eService.find(entity);
            if (e == null) {
                return Collections.emptyList();
            }

            //define a spec for reports building entity path
            Specification<ReportEntity> where = Specification.allOf(
                CommonSpecification.projectEquals(e.getProject()),
                createEntitySpecification(entity)
            );

            //fetch all reports ordered by date ASC
            Specification<ReportEntity> specification = (root, query, builder) -> {
                query.orderBy(builder.asc(root.get(AbstractEntity_.CREATED)));
                return where.toPredicate(root, query, builder);
            };

            return entityService.searchAll(specification);
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public Report createReport(@NotNull Report dto)
            throws DuplicatedEntityException, BindException, IllegalArgumentException, SystemException {
        log.debug("create report");
        try {
            //validate project
            String projectId = dto.getProject();
            if (!StringUtils.hasText(projectId) || projectService.find(projectId) == null) {
                throw new IllegalArgumentException("invalid or missing project");
            }

            try {
                //parse base spec to resolve report
                ReportBaseSpec spec = new ReportBaseSpec();
                spec.configure(dto.getSpec());

                String entity = spec.getEntity();
                if (!StringUtils.hasText(entity)) {
                    throw new IllegalArgumentException("missing or invalid entity");
                }
                String entityType = spec.getEntityType();
                if (!StringUtils.hasText(entityType)) {
                    throw new IllegalArgumentException("missing or invalid entity");
                }

                EntityService<?, ?> eService = getEntityService(entityType);
                BaseEntity e = (BaseEntity)eService.find(entity);
                if (e == null) {
                    throw new IllegalArgumentException("missing or invalid entity");
                }

                if (!projectId.equals(e.getProject())) {
                    throw new IllegalArgumentException("project mismatch");
                }

                //create as new
                return entityService.create(dto);
            } catch (DuplicatedEntityException e) {
                throw new DuplicatedEntityException(EntityName.LOG.toString(), dto.getId());
            }
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public Report updateReport(@NotNull String id, @NotNull Report dto)
            throws NoSuchEntityException, BindException, IllegalArgumentException, SystemException {
        log.debug("update report with id {}", String.valueOf(id));
        try {
            //fetch current and merge
            Report current = entityService.get(id);

            //hardcoded: entity ref is not modifiable
            Map<String, Serializable> specMap = new HashMap<>();
            if (dto.getSpec() != null) {
                specMap.putAll(dto.getSpec());
            }
            if (current.getSpec() != null) {
                specMap.put("entity", current.getSpec().get("entity"));
                specMap.put("entityType", current.getSpec().get("entityType"));
            }

            //update spec
            dto.setSpec(specMap);

            //full update, log is modifiable
            return entityService.update(id, dto);
        } catch (NoSuchEntityException e) {
            throw new NoSuchEntityException(EntityName.LOG.toString());
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }    }

    @Override
    public void deleteReport(@NotNull String id) throws SystemException {
        log.debug("delete report with id {}", String.valueOf(id));
        try {
            Report report = findReport(id);
            if (report != null) {
                //delete the report
                entityService.delete(id);
            }
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public void deleteReports(@NotNull String project, @NotNull String name) throws SystemException {
        log.debug("delete reports for project {} with name {}", project, name);

        Specification<ReportEntity> spec = Specification.allOf(
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
    public void deleteReportsByProject(@NotNull String project) throws SystemException {
        log.debug("delete reports for project {}", project);
        try {
            entityService.deleteAll(CommonSpecification.projectEquals(project));
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }

    }

    @Override
    public void deleteReportsByEntity(@NotNull String entity, @NotNull String entityType) throws SystemException {
        log.debug("delete logs for run {}", entity);
        getReportsByEntity(entity, entityType).forEach(report -> deleteReport(report.getId()));
    }

    

    @Override
    public UploadInfo completeMultiPartUpload(String id, @NotNull String project, @NotNull String filename, @NotNull String uploadId,
            @NotNull List<String> eTagPartList) throws NoSuchEntityException, SystemException {
        log.debug("complete upload url for report {}: {}", String.valueOf(id), filename);
        try {
            String path = getOrCreatePath(id, project, filename);
            UploadInfo info = filesService.completeMultiPartUpload(path, uploadId, eTagPartList);
            if (log.isTraceEnabled()) {
                log.trace("complete upload url for model with path {}: {}", path, info);
            }

            return info;
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }



    @Override
    public DownloadInfo downloadFileAsUrl(@NotNull String id) throws NoSuchEntityException, SystemException {
        log.debug("download url for report with id {}", String.valueOf(id));

        try {
            String path = getPath(id);
            DownloadInfo info = filesService.getDownloadAsUrl(path);
            if (log.isTraceEnabled()) {
                log.trace("download url for entity with id {}: {} -> {}", id, path, info);
            }

            return info;
        } catch (NoSuchEntityException e) {
            throw new NoSuchEntityException(EntityName.REPORT.toString());
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }    
    }

    @Override
    public DownloadInfo downloadFileAsUrl(@NotNull String id, @NotNull String sub)
            throws NoSuchEntityException, SystemException {
        log.debug("download url for report file with id {} and path {}", String.valueOf(id), String.valueOf(sub));

        try {
            String path = getPath(id);

            String fullPath = Optional
                .ofNullable(sub)
                .map(s -> {
                    //build sub path *only* if not matching spec path
                    return path.endsWith(sub) ? path : path + sub;
                })
                .orElse(path);

            DownloadInfo info = filesService.getDownloadAsUrl(fullPath);
            if (log.isTraceEnabled()) {
                log.trace("download url for model with id {} and path {}: {} -> {}", id, sub, path, info);
            }

            return info;
        } catch (NoSuchEntityException e) {
            throw new NoSuchEntityException(EntityName.REPORT.toString());
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }


    @Override
    public List<FileInfo> getFileInfo(@NotNull String id) throws NoSuchEntityException, SystemException {
        log.debug("get storage metadata for report with id {}", String.valueOf(id));
        try {
            Report entity = entityService.get(id);
            StatusFieldAccessor statusFieldAccessor = StatusFieldAccessor.with(entity.getStatus());
            List<FileInfo> files = statusFieldAccessor.getFiles();

            if (files == null || files.isEmpty()) {
                FilesInfo filesInfo = filesInfoService.getFilesInfo(EntityName.REPORT.getValue(), id);
                if (filesInfo != null && (filesInfo.getFiles() != null)) {
                    files = filesInfo.getFiles();
                } else {
                    files = null;
                }
            }

            if (files == null) {
                //extract path from spec
                ReportBaseSpec spec = new ReportBaseSpec();
                spec.configure(entity.getSpec());

                String path = spec.getPath();
                if (!StringUtils.hasText(path)) {
                    throw new NoSuchEntityException("file");
                }

                files = filesService.getFileInfo(path);
            }

            if (files == null) {
                files = Collections.emptyList();
            }

            if (log.isTraceEnabled()) {
                log.trace("files info for entity with id {}: {} -> {}", id, EntityName.MODEL.getValue(), files);
            }

            return files;
        } catch (NoSuchEntityException e) {
            throw new NoSuchEntityException(EntityName.REPORT.toString());
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }


    @Override
    public UploadInfo startMultiPartUpload(String id, @NotNull String project, @NotNull String filename)
            throws NoSuchEntityException, SystemException {
                log.debug("start upload url for report with id {}: {}", String.valueOf(id), filename);

                try {
                    String path = getOrCreatePath(id, project, filename);
        
                    UploadInfo info = filesService.startMultiPartUpload(path);
                    if (log.isTraceEnabled()) {
                        log.trace("start upload url for model with id {}: {}", id, info);
                    }
        
                    return info;
                } catch (StoreException e) {
                    log.error("store error: {}", e.getMessage());
                    throw new SystemException(e.getMessage());
                }
    }


    @Override
    public void storeFileInfo(@NotNull String id, List<FileInfo> files) throws SystemException {
        try {
            Report entity = entityService.get(id);
            if (files != null) {
                log.debug("store files info for {}", entity.getId());
                filesInfoService.saveFilesInfo(EntityName.REPORT.getValue(), id, files);
            }
        } catch (NoSuchEntityException e) {
            throw new NoSuchEntityException(EntityName.REPORT.getValue());
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }


    @Override
    public UploadInfo uploadFileAsUrl(String id, @NotNull String project, @NotNull String filename)
            throws NoSuchEntityException, SystemException {
        log.debug("upload url for report with id {}: {}", String.valueOf(id), filename);

        try {
            String path = getOrCreatePath(id, project, filename);
            UploadInfo info = filesService.getUploadAsUrl(path);
            if (log.isTraceEnabled()) {
                log.trace("upload url for model with id {}: {}", id, info);
            }

            return info;
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }


    @Override
    public UploadInfo uploadMultiPart(String id, @NotNull String project, @NotNull String filename, @NotNull String uploadId,
            @NotNull Integer partNumber) throws NoSuchEntityException, SystemException {
        log.debug("upload part url for report {}: {}", String.valueOf(id), filename);
        try {
            String path = getOrCreatePath(id, project, filename);
            UploadInfo info = filesService.uploadMultiPart(path, uploadId, partNumber);
            if (log.isTraceEnabled()) {
                log.trace("part upload url for model with path {}: {}", path, info);
            }

            return info;
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }


    private EntityService<? extends BaseDTO, ? extends BaseEntity> getEntityService(String entityType) {
        EntityName entityName = EntityName.valueOf(entityType);
        switch (entityName) {
            case DATAITEM:
                return dataItemEntityService;
            case ARTIFACT:
                return artifactEntityService;
            case FUNCTION:
                return functionEntityService;
            case WORKFLOW:
                return workflowEntityService;
            case MODEL:
                return modelEntityService;
            default:
                throw new IllegalArgumentException("Unsupported report entity type: " + entityType);
        }
    }

    private Specification<ReportEntity> createEntitySpecification(String entity) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("entity"), entity);
        };
    }

    private String getOrCreatePath(String id, String project, String filename) throws StoreException {
        String path =
            filesService.getDefaultStore() +
            "/" +
            project +
            "/" +
            EntityName.REPORT.getValue() +
            "/" +
            id +
            "/" +
            (filename.startsWith("/") ? filename : "/" + filename);

        //report may not exists (yet)
        Report report = entityService.find(id);

        if (report != null) {
            //extract path from spec
            ReportBaseSpec spec = new ReportBaseSpec();
            spec.configure(report.getSpec());

            path = spec.getPath();
            if (!StringUtils.hasText(path)) {
                throw new NoSuchEntityException("file");
            }
        }
        return path;
    }


    private String getPath(String id) throws StoreException {
        Report entity = entityService.get(id);
        //extract path from spec
        ReportBaseSpec spec = new ReportBaseSpec(); 
        spec.configure(entity.getSpec());

        String path = spec.getPath();
        if (!StringUtils.hasText(path)) {
            throw new NoSuchEntityException("file");
        }
        return path;
    }

}
