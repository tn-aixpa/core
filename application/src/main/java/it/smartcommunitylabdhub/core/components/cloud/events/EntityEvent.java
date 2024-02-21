package it.smartcommunitylabdhub.core.components.cloud.events;

import it.smartcommunitylabdhub.commons.models.base.BaseDTO;
import java.io.Serializable;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EntityEvent<T extends BaseDTO> extends ApplicationEvent implements Serializable {

    private final Class<T> clazz;
    private final BaseDTO entity;
    private final EntityAction action;

    public EntityEvent(BaseDTO entity, Class<T> clazz, EntityAction action) {
        super(entity);
        this.clazz = clazz;
        this.entity = entity;
        this.action = action;
    }

    public T getEntity() {
        return (T) entity;
    }
}
