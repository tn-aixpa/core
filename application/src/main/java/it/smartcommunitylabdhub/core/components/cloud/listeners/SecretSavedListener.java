package it.smartcommunitylabdhub.core.components.cloud.listeners;

import it.smartcommunitylabdhub.commons.models.entities.secret.Secret;
import it.smartcommunitylabdhub.core.components.cloud.events.EntityAction;
import it.smartcommunitylabdhub.core.components.cloud.events.EntityEvent;
import it.smartcommunitylabdhub.core.models.builders.secret.SecretDTOBuilder;
import it.smartcommunitylabdhub.core.models.entities.secret.SecretEntity;
import it.smartcommunitylabdhub.core.models.entities.secret.SecretEntity;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class SecretSavedListener {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private SecretDTOBuilder secretDTOBuilder;

    @PostPersist
    public void onPostPersist(Object entity) {
        // Trigger a custom event when an entity is saved
        eventPublisher.publishEvent(
            new EntityEvent<>(secretDTOBuilder.build((SecretEntity) entity, false), Secret.class, EntityAction.CREATE)
        );
    }

    @PostUpdate
    public void onPostUpdate(Object entity) {
        // Trigger a custom event when an entity is removed
        eventPublisher.publishEvent(
            new EntityEvent<>(secretDTOBuilder.build((SecretEntity) entity, false), Secret.class, EntityAction.UPDATE)
        );
    }

    @PostRemove
    public void onPostRemove(Object entity) {
        // Trigger a custom event when an entity is removed
        eventPublisher.publishEvent(
            new EntityEvent<>(secretDTOBuilder.build((SecretEntity) entity, false), Secret.class, EntityAction.UPDATE)
        );
    }
}
