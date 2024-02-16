package it.smartcommunitylabdhub.core.components.cloud.events;

import it.smartcommunitylabdhub.commons.models.base.BaseEntity;
import java.io.Serializable;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EntityEvent<T extends BaseEntity> extends ApplicationEvent implements Serializable {

    private final Class<T> clazz;
    private final Object entity;
    private final EntityAction action;

    public EntityEvent(Object entity, Class<T> clazz, EntityAction action) {
        super(entity);
        this.clazz = clazz;
        this.entity = entity;
        this.action = action;
    }

    public T getEntity() {
        return (T) entity;
    }
}
