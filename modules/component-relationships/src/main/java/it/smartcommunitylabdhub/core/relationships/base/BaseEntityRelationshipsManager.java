package it.smartcommunitylabdhub.core.relationships.base;

import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import it.smartcommunitylabdhub.commons.models.base.BaseDTO;
import it.smartcommunitylabdhub.commons.models.entities.EntityName;
import it.smartcommunitylabdhub.commons.models.metadata.MetadataDTO;
import it.smartcommunitylabdhub.commons.models.metadata.RelationshipsMetadata;
import it.smartcommunitylabdhub.commons.models.relationships.RelationshipDetail;
import it.smartcommunitylabdhub.core.models.base.BaseEntity;
import it.smartcommunitylabdhub.core.relationships.EntityRelationshipsManager;
import it.smartcommunitylabdhub.relationships.RelationshipsEntityService;
import it.smartcommunitylabdhub.relationships.persistence.RelationshipEntity;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;

@Slf4j
public abstract class BaseEntityRelationshipsManager<D extends BaseDTO & MetadataDTO, E extends BaseEntity>
    implements EntityRelationshipsManager<E>, InitializingBean {

    protected RelationshipsEntityService service;
    protected Converter<E, D> builder;

    @Autowired
    public void setBuilder(Converter<E, D> builder) {
        this.builder = builder;
    }

    @Autowired
    public void setService(RelationshipsEntityService service) {
        this.service = service;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(builder, "builder can not be null");
    }

    //TODO remove and infer type from generic!
    protected abstract EntityName getType();

    @Override
    public void register(E entity) throws StoreException {
        Assert.notNull(entity, "entity can not be null");

        D item = builder.convert(entity);
        if (item == null) {
            throw new IllegalArgumentException("invalid or null entity");
        }

        log.debug("register for {} {}", getType(), entity.getId());

        RelationshipsMetadata relationships = RelationshipsMetadata.from(item.getMetadata());
        service.register(item.getProject(), getType(), item.getId(), item.getKey(), relationships.getRelationships());
    }

    @Override
    public void clear(E entity) throws StoreException {
        Assert.notNull(entity, "entity can not be null");

        D item = builder.convert(entity);
        if (item == null) {
            throw new IllegalArgumentException("invalid or null entity");
        }

        log.debug("clear for {} {}", getType(), entity.getId());

        service.clear(item.getProject(), getType(), item.getId());
    }

    @Override
    public List<RelationshipDetail> getRelationships(E entity) throws StoreException {
        Assert.notNull(entity, "entity can not be null");

        log.debug("get for artifact {}", entity.getId());

        List<RelationshipEntity> entries = service.listByEntity(entity.getProject(), getType(), entity.getId());
        return entries
            .stream()
            .map(e -> new RelationshipDetail(e.getRelationship(), e.getSourceKey(), e.getDestKey()))
            .toList();
    }
}
