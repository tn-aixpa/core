package it.smartcommunitylabdhub.core.models.builders.task;

import it.smartcommunitylabdhub.commons.models.entities.task.Task;
import it.smartcommunitylabdhub.commons.models.entities.task.TaskMetadata;
import it.smartcommunitylabdhub.commons.utils.MapUtils;
import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.converters.types.MetadataConverter;
import it.smartcommunitylabdhub.core.models.entities.task.TaskEntity;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    public Task build(TaskEntity task) {
        return EntityFactory.create(
            Task::new,
            task,
            builder ->
                builder
                    .with(dto -> dto.setId(task.getId()))
                    .with(dto -> dto.setKind(task.getKind()))
                    .with(dto -> dto.setProject(task.getProject()))
                    .with(dto -> {
                        // Set Metadata for task
                        TaskMetadata taskMetadata = Optional
                            .ofNullable(metadataConverter.reverseByClass(task.getMetadata(), TaskMetadata.class))
                            .orElseGet(TaskMetadata::new);
                        taskMetadata.setVersion(task.getId());
                        taskMetadata.setProject(task.getProject());
                        taskMetadata.setCreated(task.getCreated());
                        taskMetadata.setUpdated(task.getUpdated());
                        dto.setMetadata(taskMetadata);
                    })
                    .with(dto -> dto.setSpec(ConversionUtils.reverse(task.getSpec(), "cbor")))
                    .with(dto -> dto.setExtra(ConversionUtils.reverse(task.getExtra(), "cbor")))
                    .with(dto ->
                        dto.setStatus(
                            MapUtils.mergeMultipleMaps(
                                ConversionUtils.reverse(task.getStatus(), "cbor"),
                                Map.of("state", task.getState())
                            )
                        )
                    )
        );
    }
}
