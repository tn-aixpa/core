package it.smartcommunitylabdhub.core.components.cloud.listeners;

import it.smartcommunitylabdhub.core.components.cloud.events.EntitySavedEvent;
import it.smartcommunitylabdhub.core.models.entities.workflow.WorkflowEntity;
import jakarta.persistence.PostPersist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class WorkflowSavedListener {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @PostPersist
    public void onPostPersist(Object entity) {
        // Trigger a custom event when an entity is saved
        eventPublisher.publishEvent(new EntitySavedEvent<>(entity, WorkflowEntity.class));
    }
}
