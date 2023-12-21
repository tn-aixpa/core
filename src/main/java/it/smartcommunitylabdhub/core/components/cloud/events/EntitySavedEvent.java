package it.smartcommunitylabdhub.core.components.cloud.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EntitySavedEvent<T> extends ApplicationEvent {

    private final T entity;

    public EntitySavedEvent(Object source, T entity) {
        super(source);
        this.entity = entity;
    }

}
