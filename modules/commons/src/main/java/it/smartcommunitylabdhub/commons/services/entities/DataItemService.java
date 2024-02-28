package it.smartcommunitylabdhub.commons.services.entities;

import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.models.entities.dataitem.DataItem;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

/*
 * Service for managing data items
 */
public interface DataItemService {
    /**
     * List all dataItems
     * @param pageable
     * @return
     */
    Page<DataItem> listDataItems(Pageable pageable);

    /**
     * List the latest version of every dataItem
     * @param project
     * @param pageable
     * @return
     */
    Page<DataItem> listLatestDataItemsByProject(@NotNull String project, Pageable pageable);

    /**
     * Find all versions of a given dataItem
     * @param project
     * @param name
     * @return
     */
    List<DataItem> findDataItems(@NotNull String project, @NotNull String name);

    /**
     * Find all versions of a given dataItem
     * @param project
     * @param name
     * @param pageable
     * @return
     */
    Page<DataItem> findDataItems(@NotNull String project, @NotNull String name, Pageable pageable);

    /**
     * Find a specific dataItem (version) via unique ID. Returns null if not found
     * @param id
     * @return
     */
    @Nullable
    DataItem findDataItem(@NotNull String id);

    /**
     * Get a specific dataItem (version) via unique ID. Throws exception if not found
     * @param id
     * @return
     * @throws NoSuchEntityException
     */
    DataItem getDataItem(@NotNull String id) throws NoSuchEntityException;

    /**
     * Get the latest version of a given dataItem
     * @param project
     * @param name
     * @return
     * @throws NoSuchEntityException
     */
    DataItem getLatestDataItem(@NotNull String project, @NotNull String name) throws NoSuchEntityException;

    /**
     * Create a new dataItem and store it
     * @param dataItemDTO
     * @return
     * @throws DuplicatedEntityException
     */
    DataItem createDataItem(@NotNull DataItem dataItemDTO) throws DuplicatedEntityException;

    /**
     * Update a specific dataItem version
     * @param id
     * @param dataItemDTO
     * @return
     * @throws NoSuchEntityException
     */
    DataItem updateDataItem(@NotNull String id, @NotNull DataItem dataItemDTO) throws NoSuchEntityException;

    /**
     * Delete a specific dataItem (version) via unique ID
     * @param id
     */
    void deleteDataItem(@NotNull String id);

    /**
     * Delete all versions of a given dataItem
     * @param project
     * @param name
     */
    void deleteDataItems(@NotNull String project, @NotNull String name);
}
