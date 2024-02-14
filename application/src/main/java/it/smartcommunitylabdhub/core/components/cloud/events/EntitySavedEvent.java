package it.smartcommunitylabdhub.core.components.cloud.events;

import it.smartcommunitylabdhub.commons.models.base.BaseEntity;
import java.io.Serializable;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EntitySavedEvent<T extends BaseEntity> extends ApplicationEvent implements Serializable {

    private final Class<T> clazz;
    private final Object entity;

    public EntitySavedEvent(Object entity, Class<T> clazz) {
        super(entity);
        this.clazz = clazz;
        this.entity = entity;
    }

    public Class<?> getEventClass() {
        return this.getClazz();
    }

    public String getEventClassName() {
        return this.getClazz().getName();
    }
}
