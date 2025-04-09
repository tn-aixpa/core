package it.smartcommunitylabdhub.commons.services;

import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.exceptions.SystemException;
import it.smartcommunitylabdhub.commons.models.artifact.Artifact;
import it.smartcommunitylabdhub.commons.models.report.Report;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindException;

/*
 * Service for managing reports
 */
public interface ReportService {
    /**
     * List all reports
     * @param pageable
     * @return
     */
    Page<Report> listReports(Pageable pageable) throws SystemException;

    /**
     * List the latest version of every report
     * @return
     */
    List<Report> listLatestReports() throws SystemException;

     /**
     * List the latest version of every report
     * @param pageable
     * @return
     */
    Page<Report> listLatestReports(Pageable pageable) throws SystemException;

    /**
     * List all versions of every report for a user
     * @param user
     * @return
     */
    List<Report> listReportsByUser(@NotNull String user) throws SystemException;

    /**
     * List all versions of every report for a project
     * @param project
     * @return
     */
    List<Report> listReportsByProject(@NotNull String project) throws SystemException;

    /**
     * List all versions of every report for a project
     * @param project
     * @param pageable
     * @return
     */
    Page<Report> listReportsByProject(@NotNull String project, Pageable pageable) throws SystemException;

    /**
     * List the latest version of every report for a project
     * @param project
     * @return
     */
    List<Report> listLatestReportsByProject(@NotNull String project) throws SystemException;

        /**
     * List the latest version of every report for a project
     * @param project
     * @param pageable
     * @return
     */
    Page<Report> listLatestReportByProject(@NotNull String project, Pageable pageable) throws SystemException;

    /**
     * List all reports for a given entity
     * @param entity
     * @param entityType
     * @return
     */
    List<Report> getReportsByEntity(@NotNull String entity, @NotNull String entityType) throws SystemException;

    /**
     * List all reports for a given entity
     * @param entity
     * @param entityType
     * @return
     */
    List<Report> getLatestReportsByEntity(@NotNull String entity, @NotNull String entityType) throws SystemException;

    /**
     * Find a specific report (version) via unique ID. Returns null if not found
     * @param id
     * @return
     */
    @Nullable
    Report findReport(@NotNull String id) throws SystemException;

    /**
     * Find all versions of a given report
     * @param project
     * @param name
     * @return
     */
    List<Report> findReports(@NotNull String project, @NotNull String name) throws SystemException;

    /**
     * Find all versions of a given report
     * @param project
     * @param name
     * @param pageable
     * @return
     */
    Page<Report> findReports(@NotNull String project, @NotNull String name, Pageable pageable) throws SystemException;

    /**
     * Get a specific report (version) via unique ID. Throws exception if not found
     * @param id
     * @return
     * @throws NoSuchEntityException
     */
    Report getReport(@NotNull String id) throws NoSuchEntityException, SystemException;

    /**
     * Get the latest version of a given report
     * @param project
     * @param name
     * @return
     * @throws NoSuchEntityException
     */
    Report getLatestReport(@NotNull String project, @NotNull String name)
        throws NoSuchEntityException, SystemException;

    /**
     * Create a new report and store it
     * @param reportDTO
     * @return
     * @throws DuplicatedEntityException
     */
    Report createReport(@NotNull Report reportDTO)
        throws DuplicatedEntityException, BindException, IllegalArgumentException, SystemException;

    /**
     * Update a specific report version
     * @param id
     * @param reportDTO
     * @return
     * @throws NoSuchEntityException
     */
    Report updateReport(@NotNull String id, @NotNull Report reportDTO)
        throws NoSuchEntityException, BindException, IllegalArgumentException, SystemException;

    /**
     * Delete a specific report (version) via unique ID
     * @param id
     */
    void deleteReport(@NotNull String id) throws SystemException;

    /**
     * Delete all versions of a given report
     * @param project
     * @param name
     */
    void deleteReports(@NotNull String project, @NotNull String name) throws SystemException;

    /**
     * Delete all reports for a given project, with cascade.
     * @param project
     */
    void deleteReportsByProject(@NotNull String project) throws SystemException;

    /**
     * Delete all reports for a given entity, with cascade.
     * @param entity
     * @param entity
     */
    void deleteReportsByEntity(@NotNull String entity, @NotNull String entityType) throws SystemException;
}
