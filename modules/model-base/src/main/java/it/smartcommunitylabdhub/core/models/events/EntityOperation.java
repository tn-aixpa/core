package it.smartcommunitylabdhub.core.models.events;

import it.smartcommunitylabdhub.commons.models.base.BaseDTO;
import org.springframework.context.ApplicationEvent;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;
import org.springframework.util.Assert;

public class EntityOperation<T extends BaseDTO> extends ApplicationEvent implements ResolvableTypeProvider {

    private final EntityAction action;
    private final T dto;

    public EntityOperation(T dto, EntityAction action) {
        super(dto);
        Assert.notNull(action, "action can not be null");
        this.action = action;
        this.dto = dto;
    }

    public T getDto() {
        return dto;
    }

    public EntityAction getAction() {
        return action;
    }

    @Override
    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(getClass(), ResolvableType.forInstance(this.dto));
    }
}
