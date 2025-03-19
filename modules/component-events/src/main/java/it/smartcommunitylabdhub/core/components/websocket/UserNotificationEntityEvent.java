package it.smartcommunitylabdhub.core.websocket;

import it.smartcommunitylabdhub.commons.models.base.BaseDTO;
import it.smartcommunitylabdhub.core.models.events.EntityAction;
import org.springframework.context.ApplicationEvent;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;
import org.springframework.util.Assert;

public class UserNotificationEntityEvent<T extends BaseDTO> extends ApplicationEvent implements ResolvableTypeProvider {

    private String user;
    private Class<T> clazz;
    private final EntityAction action;
    private final T dto;

    public UserNotificationEntityEvent(String user, T dto, Class<T> clazz, EntityAction action) {
        super(dto);
        Assert.hasText(user, "user can not be null");
        Assert.notNull(action, "action can not be null");
        Assert.notNull(clazz, "class is required");

        this.user = user;
        this.action = action;
        this.dto = dto;
        this.clazz = clazz;
    }

    public String getUser() {
        return user;
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
