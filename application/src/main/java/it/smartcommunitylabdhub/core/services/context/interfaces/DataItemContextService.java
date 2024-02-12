package it.smartcommunitylabdhub.core.services.context.interfaces;

import it.smartcommunitylabdhub.commons.models.entities.dataitem.DataItem;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DataItemContextService {
    DataItem createDataItem(String projectName, DataItem dataItemDTO);

    Page<DataItem> getByProjectNameAndDataItemName(
        Map<String, String> filter,
        String projectName,
        String dataItemName,
        Pageable pageable
    );

    Page<DataItem> getLatestByProjectName(Map<String, String> filter, String projectName, Pageable pageable);

    DataItem getByProjectAndDataItemAndUuid(String projectName, String dataItemName, String uuid);

    DataItem getLatestByProjectNameAndDataItemName(String projectName, String dataItemName);

    DataItem createOrUpdateDataItem(String projectName, String dataItemName, DataItem dataItemDTO);

    DataItem updateDataItem(String projectName, String dataItemName, String uuid, DataItem dataItemDTO);

    Boolean deleteSpecificDataItemVersion(String projectName, String dataItemName, String uuid);

    Boolean deleteAllDataItemVersions(String projectName, String dataItemName);
}
