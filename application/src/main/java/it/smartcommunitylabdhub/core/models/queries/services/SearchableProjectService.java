package it.smartcommunitylabdhub.core.models.queries.services;

import it.smartcommunitylabdhub.commons.models.entities.project.Project;
import it.smartcommunitylabdhub.commons.models.queries.SearchFilter;
import it.smartcommunitylabdhub.commons.services.entities.ProjectService;
import it.smartcommunitylabdhub.core.models.entities.project.ProjectEntity;
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
    Page<Project> searchProjects(Pageable pageable, @Nullable SearchFilter<ProjectEntity> filter);
}
