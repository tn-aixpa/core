package it.smartcommunitylabdhub.core.models.queries.services;

import it.smartcommunitylabdhub.commons.models.entities.model.Model;
import it.smartcommunitylabdhub.commons.models.queries.SearchFilter;
import it.smartcommunitylabdhub.commons.services.entities.ModelService;
import it.smartcommunitylabdhub.core.models.entities.ModelEntity;
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
    Page<Model> searchModels(Pageable pageable, @Nullable SearchFilter<ModelEntity> filter);

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
    );

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
    );
}
