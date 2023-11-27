package it.smartcommunitylabdhub.core.models.builders.task;

import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.converters.types.MetadataConverter;
import it.smartcommunitylabdhub.core.models.entities.task.TaskEntity;
import it.smartcommunitylabdhub.core.models.entities.task.XTask;
import it.smartcommunitylabdhub.core.models.entities.task.metadata.TaskMetadata;
import it.smartcommunitylabdhub.core.models.enums.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public class TaskDTOBuilder {

    @Autowired
    MetadataConverter<TaskMetadata> metadataConverter;

    /**
     * Build a taskDTO given a task
     *
     * @param task the Task
     * @return TaskDTO
     */
    public XTask build(TaskEntity task) {

        return EntityFactory.create(XTask::new, task, builder -> builder
                .with(dto -> dto.setId(task.getId()))
                .with(dto -> dto.setProject(task.getProject()))
                .with(dto -> dto.setKind(task.getKind()))
                .with(dto -> dto.setMetadata(Optional
                        .ofNullable(metadataConverter.reverseByClass(
                                task.getMetadata(),
                                TaskMetadata.class))
                        .orElseGet(TaskMetadata::new)

                ))
                .with(dto -> dto.setSpec(ConversionUtils.reverse(
                        task.getSpec(), "cbor")))
                .with(dto -> dto.setExtra(ConversionUtils.reverse(
                        task.getExtra(), "cbor")))
                .with(dto -> dto.setCreated(task.getCreated()))
                .with(dto -> dto.setUpdated(task.getUpdated()))
                .with(dto -> dto.setState(task.getState() == null
                        ? State.CREATED.name()
                        : task.getState()
                        .name()))

        );
    }
}
