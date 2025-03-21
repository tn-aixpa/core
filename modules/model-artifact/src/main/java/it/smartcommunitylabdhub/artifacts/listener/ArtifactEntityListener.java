package it.smartcommunitylabdhub.artifacts.listener;

import it.smartcommunitylabdhub.artifacts.persistence.ArtifactEntity;
import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import it.smartcommunitylabdhub.commons.models.artifact.Artifact;
import it.smartcommunitylabdhub.commons.models.project.Project;
import it.smartcommunitylabdhub.core.models.events.EntityEvent;
import it.smartcommunitylabdhub.core.models.listeners.AbstractEntityListener;
import it.smartcommunitylabdhub.core.models.service.EntityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.convert.converter.Converter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class ArtifactEntityListener extends AbstractEntityListener<ArtifactEntity, Artifact> {

    private EntityService<Project> projectService;

    public ArtifactEntityListener(Converter<ArtifactEntity, Artifact> converter) {
        super(converter);
    }

    @Autowired
    public void setProjectService(EntityService<Project> projectService) {
        this.projectService = projectService;
    }

    @Async
    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void receive(EntityEvent<ArtifactEntity> event) {
        if (event.getEntity() == null) {
            return;
        }

        //handle
        super.handle(event);

        //update project date
        if (projectService != null) {
            String projectId = event.getEntity().getProject();
            log.debug("touch update project {}", projectId);

            try {
                Project project = projectService.find(projectId);
                if (project != null) {
                    //touch to set updated
                    projectService.update(project.getId(), project);
                }
            } catch (StoreException e) {
                log.error("store error", e.getMessage());
            }
        }
    }
}
