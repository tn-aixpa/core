
package it.smartcommunitylabdhub.core.models.queries.services;

import it.smartcommunitylabdhub.commons.exceptions.SystemException;
import it.smartcommunitylabdhub.commons.models.queries.SearchFilter;
import it.smartcommunitylabdhub.commons.models.report.Report;
import it.smartcommunitylabdhub.commons.services.ReportService;
import it.smartcommunitylabdhub.core.models.entities.ReportEntity;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/*
 * Searchable service for managing reports
 */
public interface SearchableReportService extends ReportService {
    /**
     * List all reports, with optional filters
     * @param pageable
     * @param filter
     * @return
     */
    Page<Report> searchReports(Pageable pageable, @Nullable SearchFilter<ReportEntity> filter) throws SystemException;

        /**
     * List all reports, with optional filters
     * @param pageable
     * @param filter
     * @return
     */
    Page<Report> searchLatestReports(Pageable pageable, @Nullable SearchFilter<ReportEntity> filter) throws SystemException;


    /**
     * List the latest version of every report, with optional filters
     * @param project
     * @param pageable
     * @param filter
     * @return
     */
    Page<Report> searchReportsByProject(@NotNull String project, Pageable pageable, @Nullable SearchFilter<ReportEntity> filter)
        throws SystemException;

    /**
     * List the latest version of every report, with optional filters
     * @param project
     * @param pageable
     * @param filter
     * @return
     */
    Page<Report> searchLatestReportsByProject(@NotNull String project, Pageable pageable, @Nullable SearchFilter<ReportEntity> filter)
        throws SystemException;

}
