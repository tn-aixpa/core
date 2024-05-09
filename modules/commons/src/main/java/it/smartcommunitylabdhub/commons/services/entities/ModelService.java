package it.smartcommunitylabdhub.commons.services.entities;

import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.models.entities.model.Model;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

/*
 * Service for managing data items
 */
public interface ModelService {
    /**
     * List all models
     * @param pageable
     * @return
     */
    Page<Model> listModels(Pageable pageable);

    /**
     * List all versions of every model for a user
     * @param user
     * @return
     */
    List<Model> listModelsByUser(@NotNull String user);

    /**
     * List all versions of every model for a project
     * @param project
     * @return
     */
    List<Model> listModelsByProject(@NotNull String project);

    /**
     * List all versions of every model for a project
     * @param project
     * @param pageable
     * @return
     */
    Page<Model> listModelsByProject(@NotNull String project, Pageable pageable);

    /**
     * List the latest version of every model for a project
     * @param project
     * @return
     */
    List<Model> listLatestModelsByProject(@NotNull String project);

    /**
     * List the latest version of every model for a project
     * @param project
     * @param pageable
     * @return
     */
    Page<Model> listLatestModelsByProject(@NotNull String project, Pageable pageable);

    /**
     * Find all versions of a given model
     * @param project
     * @param name
     * @return
     */
    List<Model> findModels(@NotNull String project, @NotNull String name);

    /**
     * Find all versions of a given model
     * @param project
     * @param name
     * @param pageable
     * @return
     */
    Page<Model> findModels(@NotNull String project, @NotNull String name, Pageable pageable);

    /**
     * Find a specific model (version) via unique ID. Returns null if not found
     * @param id
     * @return
     */
    @Nullable
    Model findModel(@NotNull String id);

    /**
     * Get a specific model (version) via unique ID. Throws exception if not found
     * @param id
     * @return
     * @throws NoSuchEntityException
     */
    Model getModel(@NotNull String id) throws NoSuchEntityException;

    /**
     * Get the latest version of a given model
     * @param project
     * @param name
     * @return
     * @throws NoSuchEntityException
     */
    Model getLatestModel(@NotNull String project, @NotNull String name) throws NoSuchEntityException;

    /**
     * Create a new model and store it
     * @param modelDTO
     * @return
     * @throws DuplicatedEntityException
     */
    Model createModel(@NotNull Model modelDTO) throws DuplicatedEntityException;

    /**
     * Update a specific model version
     * @param id
     * @param modelDTO
     * @return
     * @throws NoSuchEntityException
     */
    Model updateModel(@NotNull String id, @NotNull Model modelDTO) throws NoSuchEntityException;

    /**
     * Delete a specific model (version) via unique ID
     * @param id
     */
    void deleteModel(@NotNull String id);

    /**
     * Delete all versions of a given model
     * @param project
     * @param name
     */
    void deleteModels(@NotNull String project, @NotNull String name);

    /**
     * Delete all models for a given project, with cascade.
     * @param project
     */
    void deleteModelsByProject(@NotNull String project);
}
