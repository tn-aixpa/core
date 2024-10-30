package it.smartcommunitylabdhub.core.relationships;

import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import it.smartcommunitylabdhub.commons.models.base.RelationshipDetail;
import it.smartcommunitylabdhub.commons.models.entities.artifact.Artifact;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.metadata.RelationshipsMetadata;
import it.smartcommunitylabdhub.core.models.builders.artifact.ArtifactDTOBuilder;
import it.smartcommunitylabdhub.core.models.entities.ArtifactEntity;
import it.smartcommunitylabdhub.core.models.entities.RelationshipEntity;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Slf4j
public class ArtifactEntityRelationshipsManager extends BaseEntityRelationshipsManager<ArtifactEntity> {

    private static final EntityName TYPE = EntityName.ARTIFACT;

    private final ArtifactDTOBuilder builder;

    public ArtifactEntityRelationshipsManager(ArtifactDTOBuilder builder) {
        Assert.notNull(builder, "builder can not be null");
        this.builder = builder;
    }

    @Override
    public void register(ArtifactEntity entity) throws StoreException {
        Assert.notNull(entity, "entity can not be null");

        Artifact item = builder.convert(entity);
        if (item == null) {
            throw new IllegalArgumentException("invalid or null entity");
        }

        log.debug("register for artifact {}", entity.getId());

        RelationshipsMetadata relationships = RelationshipsMetadata.from(item.getMetadata());
        service.register(item.getProject(), TYPE, item.getId(), item.getKey(), relationships.getRelationships());
    }

    @Override
    public void clear(ArtifactEntity entity) throws StoreException {
        Assert.notNull(entity, "entity can not be null");

        Artifact item = builder.convert(entity);
        if (item == null) {
            throw new IllegalArgumentException("invalid or null entity");
        }

        log.debug("clear for artifact {}", entity.getId());

        service.clear(item.getProject(), TYPE, item.getId());
    }

    @Override
    public List<RelationshipDetail> getRelationships(ArtifactEntity entity) throws StoreException {
        Assert.notNull(entity, "entity can not be null");

        log.debug("get for artifact {}", entity.getId());

        List<RelationshipEntity> entries = service.listByEntity(entity.getProject(), TYPE, entity.getId());
        return entries
            .stream()
            .map(e -> new RelationshipDetail(e.getRelationship(), e.getSourceKey(), e.getDestKey()))
            .toList();
    }
}
