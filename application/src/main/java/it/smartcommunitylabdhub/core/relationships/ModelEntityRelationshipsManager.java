package it.smartcommunitylabdhub.core.relationships;

import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import it.smartcommunitylabdhub.commons.models.base.RelationshipDetail;
import it.smartcommunitylabdhub.commons.models.entities.model.Model;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.metadata.RelationshipsMetadata;
import it.smartcommunitylabdhub.core.models.builders.model.ModelDTOBuilder;
import it.smartcommunitylabdhub.core.models.entities.ModelEntity;
import it.smartcommunitylabdhub.core.models.entities.RelationshipEntity;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Slf4j
public class ModelEntityRelationshipsManager extends BaseEntityRelationshipsManager<ModelEntity> {

    private static final EntityName TYPE = EntityName.MODEL;

    private final ModelDTOBuilder builder;

    public ModelEntityRelationshipsManager(ModelDTOBuilder builder) {
        Assert.notNull(builder, "builder can not be null");
        this.builder = builder;
    }

    @Override
    public void register(ModelEntity entity) {
        Assert.notNull(entity, "entity can not be null");

        Model item = builder.convert(entity);
        if (item == null) {
            throw new IllegalArgumentException("invalid or null entity");
        }
        try {
            log.debug("register for model {}", entity.getId());

            RelationshipsMetadata relationships = RelationshipsMetadata.from(item.getMetadata());
            service.register(item.getProject(), TYPE, item.getId(), item.getKey(), relationships.getRelationships());
        } catch (StoreException e) {
            log.error("error with service: {}", e.getMessage());
        }
    }

    @Override
    public void clear(ModelEntity entity) {
        Assert.notNull(entity, "entity can not be null");

        Model item = builder.convert(entity);
        if (item == null) {
            throw new IllegalArgumentException("invalid or null entity");
        }
        try {
            log.debug("clear for model {}", entity.getId());

            service.clear(item.getProject(), TYPE, item.getId());
        } catch (StoreException e) {
            log.error("error with service: {}", e.getMessage());
        }
    }

    @Override
    public List<RelationshipDetail> getRelationships(ModelEntity entity) throws StoreException {
        Assert.notNull(entity, "entity can not be null");

        log.debug("get for model {}", entity.getId());

        List<RelationshipEntity> entries = service.listByEntity(entity.getProject(), TYPE, entity.getId());
        return entries
            .stream()
            .map(e -> new RelationshipDetail(e.getRelationship(), e.getSourceKey(), e.getDestKey()))
            .toList();
    }
}
