package it.smartcommunitylabdhub.core.components.cloud.listeners;

import it.smartcommunitylabdhub.commons.models.entities.workflow.Workflow;
import it.smartcommunitylabdhub.core.components.cloud.events.EntityAction;
import it.smartcommunitylabdhub.core.components.cloud.events.EntityEvent;
import it.smartcommunitylabdhub.core.models.builders.workflow.WorkflowDTOBuilder;
import it.smartcommunitylabdhub.core.models.entities.workflow.WorkflowEntity;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class WorkflowSavedListener {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private WorkflowDTOBuilder workflowDTOBuilder;

    @PostPersist
    public void onPostPersist(Object entity) {
        // Trigger a custom event when an entity is saved
        eventPublisher.publishEvent(
            new EntityEvent<>(
                workflowDTOBuilder.build((WorkflowEntity) entity),
                entity,
                Workflow.class,
                EntityAction.CREATE
            )
        );
    }

    @PostUpdate
    public void onPostUpdate(Object entity) {
        // Trigger a custom event when an entity is removed
        eventPublisher.publishEvent(
            new EntityEvent<>(
                workflowDTOBuilder.build((WorkflowEntity) entity),
                entity,
                Workflow.class,
                EntityAction.UPDATE
            )
        );
    }

    @PostRemove
    public void onPostRemove(Object entity) {
        // Trigger a custom event when an entity is removed
        eventPublisher.publishEvent(
            new EntityEvent<>(
                workflowDTOBuilder.build((WorkflowEntity) entity),
                entity,
                Workflow.class,
                EntityAction.UPDATE
            )
        );
    }
}
