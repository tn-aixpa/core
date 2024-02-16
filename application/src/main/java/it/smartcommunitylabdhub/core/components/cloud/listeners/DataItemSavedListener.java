package it.smartcommunitylabdhub.core.components.cloud.listeners;

import it.smartcommunitylabdhub.commons.models.entities.dataitem.DataItem;
import it.smartcommunitylabdhub.core.components.cloud.events.EntityAction;
import it.smartcommunitylabdhub.core.components.cloud.events.EntityEvent;
import it.smartcommunitylabdhub.core.models.builders.dataitem.DataItemDTOBuilder;
import it.smartcommunitylabdhub.core.models.entities.dataitem.DataItemEntity;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class DataItemSavedListener {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private DataItemDTOBuilder dataItemDTOBuilder;

    @PostPersist
    public void onPostPersist(Object entity) {
        // Trigger a custom event when an entity is saved
        eventPublisher.publishEvent(
            new EntityEvent<>(
                dataItemDTOBuilder.build((DataItemEntity) entity, false),
                DataItem.class,
                EntityAction.CREATE
            )
        );
    }

    @PostUpdate
    public void onPostUpdate(Object entity) {
        // Trigger a custom event when an entity is removed
        eventPublisher.publishEvent(
            new EntityEvent<>(
                dataItemDTOBuilder.build((DataItemEntity) entity, false),
                DataItem.class,
                EntityAction.UPDATE
            )
        );
    }

    @PostRemove
    public void onPostRemove(Object entity) {
        // Trigger a custom event when an entity is removed
        eventPublisher.publishEvent(
            new EntityEvent<>(
                dataItemDTOBuilder.build((DataItemEntity) entity, false),
                DataItem.class,
                EntityAction.UPDATE
            )
        );
    }
}
