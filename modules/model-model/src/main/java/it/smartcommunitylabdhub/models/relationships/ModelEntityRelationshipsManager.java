package it.smartcommunitylabdhub.models.relationships;

import it.smartcommunitylabdhub.commons.models.entities.EntityName;
import it.smartcommunitylabdhub.commons.models.model.Model;
import it.smartcommunitylabdhub.core.relationships.base.BaseEntityRelationshipsManager;
import it.smartcommunitylabdhub.models.persistence.ModelEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ModelEntityRelationshipsManager extends BaseEntityRelationshipsManager<Model, ModelEntity> {

    private static final EntityName TYPE = EntityName.MODEL;

    @Override
    protected EntityName getType() {
        return TYPE;
    }
}
