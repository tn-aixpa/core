package it.smartcommunitylabdhub.commons.services;

import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.models.entities.dataitem.DataItem;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DataItemService {
    Page<DataItem> getDataItems(Map<String, String> filter, Pageable pageable);

    DataItem createDataItem(@NotNull DataItem dataItemDTO) throws DuplicatedEntityException;

    DataItem getDataItem(@NotNull String id) throws NoSuchEntityException;

    DataItem updateDataItem(@NotNull String id, @NotNull DataItem dataItemDTO) throws NoSuchEntityException;

    void deleteDataItem(@NotNull String id);
}
