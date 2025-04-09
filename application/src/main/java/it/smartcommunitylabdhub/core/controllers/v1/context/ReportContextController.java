package it.smartcommunitylabdhub.core.controllers.v1.context;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.smartcommunitylabdhub.commons.Keys;
import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.exceptions.SystemException;
import it.smartcommunitylabdhub.commons.models.report.Report;
import it.smartcommunitylabdhub.commons.models.files.FileInfo;
import it.smartcommunitylabdhub.commons.models.queries.SearchFilter;
import it.smartcommunitylabdhub.core.ApplicationKeys;
import it.smartcommunitylabdhub.core.annotations.ApiVersion;
import it.smartcommunitylabdhub.core.models.entities.ReportEntity;
import it.smartcommunitylabdhub.core.models.queries.filters.entities.ReportEntityFilter;
import it.smartcommunitylabdhub.core.models.queries.services.SearchableReportService;
import it.smartcommunitylabdhub.files.models.DownloadInfo;
import it.smartcommunitylabdhub.files.models.UploadInfo;
import it.smartcommunitylabdhub.files.service.EntityFilesService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import javax.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ApiVersion("v1")
@RequestMapping("/-/{project}/reports")
@PreAuthorize(
    "hasAuthority('ROLE_ADMIN') or (hasAuthority(#project+':ROLE_USER') or hasAuthority(#project+':ROLE_ADMIN'))"
)
@Validated
@Slf4j
@Tag(name = "Report context API", description = "Endpoints related to reports management in project")
public class ReportContextController {

    @Autowired
    SearchableReportService reportService;

    @Autowired
    EntityFilesService<Report> filesService;

    @Operation(summary = "Create a report in a project context")
    @PostMapping(
        value = "",
        consumes = { MediaType.APPLICATION_JSON_VALUE, "application/x-yaml" },
        produces = "application/json; charset=UTF-8"
    )
    public Report createReport(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @Valid @NotNull @RequestBody Report dto
    ) throws DuplicatedEntityException, IllegalArgumentException, SystemException, BindException {
        //enforce project match
        dto.setProject(project);

        //create as new
        return reportService.createReport(dto);
    }

    @Operation(summary = "Search reports")
    @GetMapping(path = "", produces = "application/json; charset=UTF-8")
    public Page<Report> searchReports(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @ParameterObject @Valid @Nullable ReportEntityFilter filter,
        @ParameterObject @RequestParam(required = false, defaultValue = "latest") String versions,
        @ParameterObject @PageableDefault(page = 0, size = ApplicationKeys.DEFAULT_PAGE_SIZE) @SortDefault.SortDefaults(
            { @SortDefault(sort = "created", direction = Direction.DESC) }
        ) Pageable pageable
    ) {
        SearchFilter<ReportEntity> sf = null;
        if (filter != null) {
            sf = filter.toSearchFilter();
        }
        if ("all".equals(versions)) {
            return reportService.searchReportsByProject(project, pageable, sf);
        } else {
            return reportService.searchLatestReportsByProject(project, pageable, sf);
        }
    }

    @Operation(summary = "Delete all version of a report")
    @DeleteMapping(path = "")
    public void deleteAllReports(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @ParameterObject @RequestParam @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String name
    ) {
        reportService.deleteReports(project, name);
    }

    /*
     * Versions
     */

    @Operation(summary = "Retrieve a specific report version given the report id")
    @GetMapping(path = "/{id}", produces = "application/json; charset=UTF-8")
    public Report getReportById(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String id
    ) throws NoSuchEntityException {
        Report report = reportService.getReport(id);

        //check for project and name match
        if (!report.getProject().equals(project)) {
            throw new IllegalArgumentException("invalid project");
        }

        return report;
    }

    @Operation(summary = "Update if exist a report in a project context")
    @PutMapping(
        value = "/{id}",
        consumes = { MediaType.APPLICATION_JSON_VALUE, "application/x-yaml" },
        produces = "application/json; charset=UTF-8"
    )
    public Report updateReportById(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String id,
        @RequestBody @Valid @NotNull Report reportDTO
    ) throws NoSuchEntityException, IllegalArgumentException, SystemException, BindException {
        Report report = reportService.getReport(id);

        //check for project and name match
        if (!report.getProject().equals(project)) {
            throw new IllegalArgumentException("invalid project");
        }

        return reportService.updateReport(id, reportDTO);
    }

    @Operation(summary = "Delete a specific report version")
    @DeleteMapping(path = "/{id}")
    public void deleteReportById(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String id
    ) throws NoSuchEntityException {
        Report report = reportService.getReport(id);

        //check for project and name match
        if (!report.getProject().equals(project)) {
            throw new IllegalArgumentException("invalid project");
        }

        reportService.deleteReport(id);
    }

    /*
     * Files
     */
    @Operation(summary = "Get download url for a given report, if available")
    @GetMapping(path = "/{id}/files/download", produces = "application/json; charset=UTF-8")
    public DownloadInfo downloadAsUrlReportById(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String id,
        @ParameterObject @RequestParam(required = false) String sub
    ) throws NoSuchEntityException {
        Report report = reportService.getReport(id);

        //check for project and name match
        if (!report.getProject().equals(project)) {
            throw new IllegalArgumentException("invalid project");
        }

        if (sub != null) {
            return filesService.downloadFileAsUrl(id, sub);
        }

        return filesService.downloadFileAsUrl(id);
    }

    @Operation(summary = "Get download url for a given report file, if available")
    @GetMapping(path = "/{id}/files/download/**", produces = "application/json; charset=UTF-8")
    public DownloadInfo downloadAsUrlFile(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String id,
        HttpServletRequest request
    ) throws NoSuchEntityException {
        Report report = reportService.getReport(id);

        //check for project and name match
        if (!report.getProject().equals(project)) {
            throw new IllegalArgumentException("invalid project");
        }
        String path = request.getRequestURL().toString().split("files/download/")[1];
        return filesService.downloadFileAsUrl(id, path);
    }

    @Operation(summary = "Create an upload url for a given report, if available")
    @PostMapping(path = "/{id}/files/upload", produces = "application/json; charset=UTF-8")
    public UploadInfo uploadAsUrlReportById(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String id,
        @RequestParam @NotNull String filename
    ) throws NoSuchEntityException {
        Report report = reportService.findReport(id);

        //check for project and name match
        if ((report != null) && !report.getProject().equals(project)) {
            throw new IllegalArgumentException("invalid project");
        }

        return filesService.uploadFileAsUrl(id, project, filename);
    }

    @Operation(summary = "Start a multipart upload for a given report, if available")
    @PostMapping(path = "/{id}/files/multipart/start", produces = "application/json; charset=UTF-8")
    public UploadInfo multipartStartUploadAsUrlReportById(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String id,
        @RequestParam @NotNull String filename
    ) throws NoSuchEntityException {
        Report report = reportService.findReport(id);

        //check for project and name match
        if ((report != null) && !report.getProject().equals(project)) {
            throw new IllegalArgumentException("invalid project");
        }

        return filesService.startMultiPartUpload(id, project, filename);
    }

    @Operation(
        summary = "Generate an upload url for a part of a given multipart upload for a given report, if available"
    )
    @PutMapping(path = "/{id}/files/multipart/part", produces = "application/json; charset=UTF-8")
    public UploadInfo multipartPartUploadAsUrlReportById(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String id,
        @RequestParam @NotNull String filename,
        @RequestParam @NotNull String uploadId,
        @RequestParam @NotNull Integer partNumber
    ) throws NoSuchEntityException {
        Report report = reportService.findReport(id);

        //check for project and name match
        if ((report != null) && !report.getProject().equals(project)) {
            throw new IllegalArgumentException("invalid project");
        }

        return filesService.uploadMultiPart(id, project, filename, uploadId, partNumber);
    }

    @Operation(summary = "Complete a multipart upload for a given report, if available")
    @PostMapping(path = "/{id}/files/multipart/complete", produces = "application/json; charset=UTF-8")
    public UploadInfo multipartCompleteUploadAsUrlReportById(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String id,
        @RequestParam @NotNull String filename,
        @RequestParam @NotNull String uploadId,
        @RequestParam @NotNull List<String> partList
    ) throws NoSuchEntityException {
        Report report = reportService.findReport(id);

        //check for project and name match
        if ((report != null) && !report.getProject().equals(project)) {
            throw new IllegalArgumentException("invalid project");
        }

        return filesService.completeMultiPartUpload(id, project, filename, uploadId, partList);
    }

    @Operation(summary = "Get file info for a given report, if available")
    @GetMapping(path = "/{id}/files/info", produces = "application/json; charset=UTF-8")
    public List<FileInfo> getReportFilesInfoById(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String id
    ) throws NoSuchEntityException {
        Report report = reportService.getReport(id);

        //check for project and name match
        if (!report.getProject().equals(project)) {
            throw new IllegalArgumentException("invalid project");
        }

        return filesService.getFileInfo(id);
    }

    @Operation(summary = "Store file info for a given entity, if available")
    @PutMapping(path = "/{id}/files/info", produces = "application/json; charset=UTF-8")
    public void storeFilesInfoById(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String id,
        @RequestBody List<FileInfo> files
    ) throws NoSuchEntityException {
        Report entity = reportService.getReport(id);

        //check for project and name match
        if (!entity.getProject().equals(project)) {
            throw new IllegalArgumentException("invalid project");
        }

        filesService.storeFileInfo(id, files);
    }

}
