package it.smartcommunitylabdhub.core.dataitems.listeners;

import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import it.smartcommunitylabdhub.commons.models.dataitem.DataItem;
import it.smartcommunitylabdhub.commons.models.entities.EntityName;
import it.smartcommunitylabdhub.commons.models.project.Project;
import it.smartcommunitylabdhub.commons.services.FilesInfoService;
import it.smartcommunitylabdhub.core.dataitems.persistence.DataItemEntity;
import it.smartcommunitylabdhub.core.models.entities.ProjectEntity;
import it.smartcommunitylabdhub.core.models.events.EntityEvent;
import it.smartcommunitylabdhub.core.models.listeners.AbstractEntityListener;
import it.smartcommunitylabdhub.core.services.EntityService;
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
public class DataItemEntityListener extends AbstractEntityListener<DataItemEntity, DataItem> {

    private EntityService<Project, ProjectEntity> projectService;
    private FilesInfoService filesInfoService;

    public DataItemEntityListener(Converter<DataItemEntity, DataItem> converter) {
        super(converter);
    }

    @Autowired
    public void setProjectService(EntityService<Project, ProjectEntity> projectService) {
        this.projectService = projectService;
    }

    @Autowired
    public void setFilesInfoService(FilesInfoService filesInfoService) {
        this.filesInfoService = filesInfoService;
    }

    @Async
    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void receive(EntityEvent<DataItemEntity> event) {
        if (event.getEntity() == null) {
            return;
        }

        log.debug("receive event for {} {}", clazz.getSimpleName(), event.getAction());

        DataItemEntity entity = event.getEntity();
        DataItemEntity prev = event.getPrev();
        if (log.isTraceEnabled()) {
            log.trace("{}: {}", clazz.getSimpleName(), String.valueOf(entity));
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

        //notify user event if either: prev == null (for create/delete), prev != null and state has changed (update)
        if (prev == null || (prev != null && prev.getState() != entity.getState())) {
            //always broadcast updates
            super.broadcast(event);

            if (entity.getUpdatedBy() != null) {
                //notify user
                super.notify(entity.getUpdatedBy(), event);

                if (!entity.getUpdatedBy().equals(entity.getCreatedBy())) {
                    //notify owner
                    super.notify(entity.getCreatedBy(), event);
                }
            }
        }
    }

    @Override
    protected void onDelete(DataItemEntity entity, DataItem dto) {
        super.onDelete(entity, dto);

        //delete files info
        if (filesInfoService != null) {
            try {
                filesInfoService.clearFilesInfo(EntityName.DATAITEM.getValue(), entity.getId());
            } catch (StoreException e) {
                log.error("store error", e.getMessage());
            }
        }
    }
}
