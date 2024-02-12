package it.smartcommunitylabdhub.core.components.cloud.events;

import java.io.Serializable;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EntitySavedEvent<T> extends ApplicationEvent implements Serializable {

    private final T entity;

    public EntitySavedEvent(Object source, T entity) {
        super(source);
        this.entity = entity;
    }

    public Class<?> getEventClass() {
        return this.entity.getClass();
    }

    public String getEventClassName() {
        return this.entity.getClass().getName();
    }
}
