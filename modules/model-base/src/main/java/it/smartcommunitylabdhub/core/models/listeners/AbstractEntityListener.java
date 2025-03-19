package it.smartcommunitylabdhub.core.models.listeners;

import it.smartcommunitylabdhub.commons.models.base.BaseDTO;
import it.smartcommunitylabdhub.core.models.base.BaseEntity;
import it.smartcommunitylabdhub.core.models.events.EntityEvent;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.convert.converter.Converter;

@Slf4j
public abstract class AbstractEntityListener<E extends BaseEntity, T extends BaseDTO> {

    protected final Converter<E, T> converter;
    protected ApplicationEventPublisher eventPublisher;
    protected final Class<T> clazz;

    protected List<EntityListener<E>> listeners = new ArrayList<>();

    protected AbstractEntityListener(Converter<E, T> converter) {
        this.converter = converter;
        clazz = extractClass();
    }

    @Autowired
    public void setEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Autowired(required = false)
    public void setListeners(List<EntityListener<E>> listeners) {
        if (listeners != null) {
            this.listeners = listeners;
        }
    }

    protected void handle(EntityEvent<E> event) {
        log.debug("receive event for {} {}", clazz.getSimpleName(), event.getAction());

        E entity = event.getEntity();
        if (log.isTraceEnabled()) {
            log.trace("{}: {}", clazz.getSimpleName(), String.valueOf(entity));
        }

        if (entity == null) {
            return;
        }

        T dto = converter.convert(entity);
        // @formatter:off
        switch (event.getAction()) {
            case CREATE: {
                    onCreate(entity, dto);
                    break;
                }
            case UPDATE: {
                    onUpdate(entity, dto);
                    break;
                }
            case DELETE: {
                    onDelete(entity, dto);
                    break;
                }
            default:
                break;
        }
        // @formatter:on
    }

    @SuppressWarnings("checkstyle:IllegalCatch")
    protected void onCreate(E entity, T dto) {
        log.debug("onCreate for {}", entity.getId());

        listeners.forEach(listener -> {
            try {
                log.trace("onCreate for {} with listener ", entity.getId(), listener.getClass().getSimpleName());
                listener.onCreate(entity);
            } catch (RuntimeException e) {
                log.error("error with listener: {}", e.getMessage());
            }
        });
    }

    @SuppressWarnings("checkstyle:IllegalCatch")
    protected void onUpdate(E entity, T dto) {
        log.debug("onUpdate for {}", entity.getId());
        listeners.forEach(listener -> {
            try {
                log.trace("onUpdate for {} with listener ", entity.getId(), listener.getClass().getSimpleName());
                listener.onUpdate(entity);
            } catch (RuntimeException e) {
                log.error("error with listener: {}", e.getMessage());
            }
        });
    }

    @SuppressWarnings("checkstyle:IllegalCatch")
    protected void onDelete(E entity, T dto) {
        log.debug("onDelete for {}", entity.getId());
        listeners.forEach(listener -> {
            try {
                log.trace("onDelete for {} with listener ", entity.getId(), listener.getClass().getSimpleName());
                listener.onDelete(entity);
            } catch (RuntimeException e) {
                log.error("error with listener: {}", e.getMessage());
            }
        });
    }

    @SuppressWarnings("unchecked")
    protected Class<T> extractClass() {
        // resolve generics type via subclass trick
        Type t = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        return (Class<T>) t;
    }
}
