package it.smartcommunitylabdhub.core.projects.service;

import it.smartcommunitylabdhub.commons.exceptions.SystemException;
import it.smartcommunitylabdhub.commons.models.project.Project;
import it.smartcommunitylabdhub.commons.models.queries.SearchFilter;
import it.smartcommunitylabdhub.commons.services.ProjectService;
import it.smartcommunitylabdhub.core.projects.persistence.ProjectEntity;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/*
 * Searchable service for managing project
 */
public interface SearchableProjectService extends ProjectService {
    /**
     * List all projects, with optional filters
     * @param pageable
     * @param filter
     * @return
     */
    Page<Project> searchProjects(Pageable pageable, @Nullable SearchFilter<ProjectEntity> filter)
        throws SystemException;
}
