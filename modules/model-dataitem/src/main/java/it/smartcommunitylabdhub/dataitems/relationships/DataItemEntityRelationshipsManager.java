package it.smartcommunitylabdhub.dataitems.relationships;

import it.smartcommunitylabdhub.commons.models.dataitem.DataItem;
import it.smartcommunitylabdhub.commons.models.entities.EntityName;
import it.smartcommunitylabdhub.core.relationships.base.BaseEntityRelationshipsManager;
import it.smartcommunitylabdhub.dataitems.persistence.DataItemEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DataItemEntityRelationshipsManager extends BaseEntityRelationshipsManager<DataItem, DataItemEntity> {

    private static final EntityName TYPE = EntityName.DATAITEM;

    @Override
    protected EntityName getType() {
        return TYPE;
    }
}
