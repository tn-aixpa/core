package it.smartcommunitylabdhub.commons.services.entities;

import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.models.entities.project.Project;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

/*
 * Service for managing projects
 */
public interface ProjectService {
    /**
     * List all projects
     * @param pageable
     * @return
     */
    Page<Project> listProjects(Pageable pageable);

    /**
     * List all projects by user
     * @param user
     * @return
     */
    List<Project> listProjectsByUser(@NotNull String user);

    /**
     * Find a specific project  via unique ID. Returns null if not found
     * @param id
     * @return
     */
    @Nullable
    Project findProject(@NotNull String id);

    /**
     * Find a specific project  via unique ID. Returns null if not found
     * @param id
     * @return
     */
    @Nullable
    Project findProjectByName(@NotNull String name);

    /**
     * Get a specific project via unique ID. Throws exception if not found
     * @param id
     * @return
     * @throws NoSuchEntityException
     */
    Project getProject(@NotNull String id) throws NoSuchEntityException;

    /**
     * Get a specific project via unique ID. Throws exception if not found
     * @param id
     * @return
     * @throws NoSuchEntityException
     */
    Project getProjectByName(@NotNull String name) throws NoSuchEntityException;

    /**
     * Create a new project and store it
     * @param projectDTO
     * @return
     * @throws DuplicatedEntityException
     */
    Project createProject(@NotNull Project projectDTO) throws DuplicatedEntityException;

    /**
     * Update a specific project
     * @param id
     * @param projectDTO
     * @return
     * @throws NoSuchEntityException
     */
    Project updateProject(@NotNull String id, @NotNull Project projectDTO) throws NoSuchEntityException;

    /**
     * Delete a specific project via unique ID, with optional cascade
     * @param id
     * @param cascade
     */
    void deleteProject(@NotNull String id, @Nullable Boolean cascade);
}
