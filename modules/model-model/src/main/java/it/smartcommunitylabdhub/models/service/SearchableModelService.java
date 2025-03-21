package it.smartcommunitylabdhub.models.service;

import it.smartcommunitylabdhub.commons.exceptions.SystemException;
import it.smartcommunitylabdhub.commons.models.model.Model;
import it.smartcommunitylabdhub.commons.models.queries.SearchFilter;
import it.smartcommunitylabdhub.commons.services.ModelService;
import it.smartcommunitylabdhub.models.persistence.ModelEntity;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/*
 * Searchable service for managing model
 */
public interface SearchableModelService extends ModelService {
    /**
     * List all models, with optional filters
     * @param pageable
     * @param filter
     * @return
     */
    Page<Model> searchModels(Pageable pageable, @Nullable SearchFilter<ModelEntity> filter) throws SystemException;

    /**
     * List the latest version of every model, with optional filters
     * @param pageable
     * @param filter
     * @return
     */
    Page<Model> searchLatestModels(Pageable pageable, @Nullable SearchFilter<ModelEntity> filter)
        throws SystemException;

    /**
     * List all versions of every model, with optional filters
     * @param project
     * @param pageable
     * @param filter
     * @return
     */
    Page<Model> searchModelsByProject(
        @NotNull String project,
        Pageable pageable,
        @Nullable SearchFilter<ModelEntity> filter
    ) throws SystemException;

    /**
     * List the latest version of every model, with optional filters
     * @param project
     * @param pageable
     * @param filter
     * @return
     */
    Page<Model> searchLatestModelsByProject(
        @NotNull String project,
        Pageable pageable,
        @Nullable SearchFilter<ModelEntity> filter
    ) throws SystemException;
}
