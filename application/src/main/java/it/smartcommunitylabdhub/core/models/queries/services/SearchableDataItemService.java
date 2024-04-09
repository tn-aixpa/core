package it.smartcommunitylabdhub.core.models.queries.services;

import it.smartcommunitylabdhub.commons.models.entities.dataitem.DataItem;
import it.smartcommunitylabdhub.commons.models.queries.SearchFilter;
import it.smartcommunitylabdhub.commons.services.entities.DataItemService;
import it.smartcommunitylabdhub.core.models.entities.DataItemEntity;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/*
 * Searchable service for managing dataItem
 */
public interface SearchableDataItemService extends DataItemService {
    /**
     * List all dataItems, with optional filters
     * @param pageable
     * @param filter
     * @return
     */
    Page<DataItem> searchDataItems(Pageable pageable, @Nullable SearchFilter<DataItemEntity> filter);

    /**
     * List all versions of every dataItem, with optional filters
     * @param project
     * @param pageable
     * @param filter
     * @return
     */
    Page<DataItem> searchDataItemsByProject(
        @NotNull String project,
        Pageable pageable,
        @Nullable SearchFilter<DataItemEntity> filter
    );

    /**
     * List the latest version of every dataItem, with optional filters
     * @param project
     * @param pageable
     * @param filter
     * @return
     */
    Page<DataItem> searchLatestDataItemsByProject(
        @NotNull String project,
        Pageable pageable,
        @Nullable SearchFilter<DataItemEntity> filter
    );
}
