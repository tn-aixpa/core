package it.smartcommunitylabdhub.core.models.listeners;

import it.smartcommunitylabdhub.commons.models.base.BaseDTO;
import it.smartcommunitylabdhub.core.components.cloud.CloudEntityEvent;
import it.smartcommunitylabdhub.core.models.base.BaseEntity;
import it.smartcommunitylabdhub.core.models.events.EntityEvent;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.convert.converter.Converter;

@Slf4j
public abstract class AbstractEntityListener<E extends BaseEntity, T extends BaseDTO> {

    protected final Converter<E, T> converter;
    protected ApplicationEventPublisher eventPublisher;
    private final Class<T> clazz;

    protected AbstractEntityListener(Converter<E, T> converter) {
        this.converter = converter;
        clazz = extractClass();
    }

    @Autowired
    public void setEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
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

        if (eventPublisher != null) {
            //publish external event
            log.debug("publish cloud event: {} for {} {}", event.getAction(), clazz.getSimpleName(), dto.getId());
            CloudEntityEvent<T> cloud = new CloudEntityEvent<>(dto, clazz, event.getAction());
            if (log.isTraceEnabled()) {
                log.trace("cloud event: {}", String.valueOf(cloud));
            }

            eventPublisher.publishEvent(cloud);
        }
    }

    @SuppressWarnings("unchecked")
    protected Class<T> extractClass() {
        // resolve generics type via subclass trick
        Type t = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        return (Class<T>) t;
    }
}
