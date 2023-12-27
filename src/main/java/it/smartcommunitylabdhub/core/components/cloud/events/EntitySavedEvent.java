package it.smartcommunitylabdhub.core.components.cloud.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.io.Serializable;

@Getter
public class EntitySavedEvent<T> extends ApplicationEvent implements Serializable {

    private final T entity;

    public EntitySavedEvent(Object source, T entity) {
        super(source);
        this.entity = entity;
    }

}
