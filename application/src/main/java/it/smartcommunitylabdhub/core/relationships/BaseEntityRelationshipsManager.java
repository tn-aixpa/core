package it.smartcommunitylabdhub.core.relationships;

import it.smartcommunitylabdhub.core.models.base.BaseEntity;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseEntityRelationshipsManager<T extends BaseEntity> implements EntityRelationshipsManager<T> {

    protected EntityRelationshipsService service;

    @Autowired
    public void setService(EntityRelationshipsService service) {
        this.service = service;
    }
}
