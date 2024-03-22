package it.smartcommunitylabdhub.core.models.events;

import it.smartcommunitylabdhub.core.models.base.BaseEntity;
import org.springframework.context.ApplicationEvent;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;
import org.springframework.util.Assert;

public class EntityEvent<T extends BaseEntity> extends ApplicationEvent implements ResolvableTypeProvider {

    private final EntityAction action;
    private final T entity;

    public EntityEvent(T entity, EntityAction action) {
        super(entity);
        Assert.notNull(action, "action can not be null");
        this.action = action;
        this.entity = entity;
    }

    public T getEntity() {
        return entity;
    }

    public EntityAction getAction() {
        return action;
    }

    @Override
    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(getClass(), ResolvableType.forInstance(this.entity));
    }
}
