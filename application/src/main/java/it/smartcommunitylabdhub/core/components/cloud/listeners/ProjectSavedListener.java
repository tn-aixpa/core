package it.smartcommunitylabdhub.core.components.cloud.listeners;

import it.smartcommunitylabdhub.commons.models.entities.project.Project;
import it.smartcommunitylabdhub.core.components.cloud.events.EntityAction;
import it.smartcommunitylabdhub.core.components.cloud.events.EntityEvent;
import it.smartcommunitylabdhub.core.models.builders.project.ProjectDTOBuilder;
import it.smartcommunitylabdhub.core.models.entities.project.ProjectEntity;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class ProjectSavedListener {

    @Autowired
    ProjectDTOBuilder projectDTOBuilder;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @PostPersist
    public void onPostPersist(Object entity) {
        // Trigger a custom event when an entity is saved
        eventPublisher.publishEvent(
            new EntityEvent<>(
                projectDTOBuilder.build(
                    (ProjectEntity) entity,
                    List.of(),
                    List.of(),
                    List.of(),
                    List.of(),
                    List.of(),
                    false
                ),
                Project.class,
                EntityAction.CREATE
            )
        );
    }

    @PostUpdate
    public void onPostUpdate(Object entity) {
        // Trigger a custom event when an entity is removed
        eventPublisher.publishEvent(
            new EntityEvent<>(
                projectDTOBuilder.build(
                    (ProjectEntity) entity,
                    List.of(),
                    List.of(),
                    List.of(),
                    List.of(),
                    List.of(),
                    false
                ),
                Project.class,
                EntityAction.UPDATE
            )
        );
    }

    @PostRemove
    public void onPostRemove(Object entity) {
        // Trigger a custom event when an entity is removed
        eventPublisher.publishEvent(
            new EntityEvent<>(
                projectDTOBuilder.build(
                    (ProjectEntity) entity,
                    List.of(),
                    List.of(),
                    List.of(),
                    List.of(),
                    List.of(),
                    false
                ),
                Project.class,
                EntityAction.UPDATE
            )
        );
    }
}
