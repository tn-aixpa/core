package it.smartcommunitylabdhub.core.services.interfaces;

import it.smartcommunitylabdhub.core.models.entities.dataitem.DataItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface DataItemService {
    Page<DataItem> getDataItems(Map<String, String> filter, Pageable pageable);

    DataItem createDataItem(DataItem dataItemDTO);

    DataItem getDataItem(String uuid);

    DataItem updateDataItem(DataItem dataItemDTO, String uuid);

    boolean deleteDataItem(String uuid);

}
