package it.smartcommunitylabdhub.core.components.cloud;

import it.smartcommunitylabdhub.commons.models.base.BaseDTO;
import it.smartcommunitylabdhub.core.models.events.EntityAction;
import org.springframework.context.ApplicationEvent;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;
import org.springframework.util.Assert;

public class CloudEntityEvent<T extends BaseDTO> extends ApplicationEvent implements ResolvableTypeProvider {

    private Class<T> clazz;
    private final EntityAction action;
    private final T dto;

    public CloudEntityEvent(T dto, Class<T> clazz, EntityAction action) {
        super(dto);
        Assert.notNull(action, "action can not be null");
        Assert.notNull(clazz, "class is required");
        this.action = action;
        this.dto = dto;
        this.clazz = clazz;
    }

    public T getDto() {
        return dto;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public EntityAction getAction() {
        return action;
    }

    @Override
    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(getClass(), ResolvableType.forInstance(this.dto));
    }
}
