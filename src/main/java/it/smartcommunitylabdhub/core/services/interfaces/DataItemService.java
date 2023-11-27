package it.smartcommunitylabdhub.core.services.interfaces;

import it.smartcommunitylabdhub.core.models.entities.dataitem.DataItem;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DataItemService {
    List<DataItem> getDataItems(Pageable pageable);

    DataItem createDataItem(DataItem dataItemDTO);

    DataItem getDataItem(String uuid);

    DataItem updateDataItem(DataItem dataItemDTO, String uuid);

    boolean deleteDataItem(String uuid);

}
