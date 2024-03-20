package it.smartcommunitylabdhub.core.models.builders.task;

import it.smartcommunitylabdhub.commons.models.entities.task.Task;
import it.smartcommunitylabdhub.commons.models.entities.task.TaskMetadata;
import it.smartcommunitylabdhub.commons.utils.MapUtils;
import it.smartcommunitylabdhub.core.models.entities.task.TaskEntity;
import jakarta.persistence.AttributeConverter;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class TaskDTOBuilder implements Converter<TaskEntity, Task> {

    private final AttributeConverter<Map<String, Serializable>, byte[]> converter;

    public TaskDTOBuilder(
        @Qualifier("cborMapConverter") AttributeConverter<Map<String, Serializable>, byte[]> cborConverter
    ) {
        this.converter = cborConverter;
    }

    /**
     * Build a taskDTO given a task
     *
     * @param entity the Task
     * @return TaskDTO
     */
    public Task build(TaskEntity entity) {
        //read metadata map as-is
        Map<String, Serializable> meta = converter.convertToEntityAttribute(entity.getMetadata());

        // build metadata
        TaskMetadata metadata = new TaskMetadata();
        metadata.configure(meta);

        if (!StringUtils.hasText(metadata.getVersion())) {
            metadata.setVersion(entity.getId());
        }
        if (!StringUtils.hasText(metadata.getName())) {
            metadata.setName(entity.getName());
        }
        metadata.setProject(entity.getProject());
        metadata.setCreated(
            entity.getCreated() != null
                ? OffsetDateTime.ofInstant(entity.getCreated().toInstant(), ZoneOffset.UTC)
                : null
        );
        metadata.setUpdated(
            entity.getUpdated() != null
                ? OffsetDateTime.ofInstant(entity.getUpdated().toInstant(), ZoneOffset.UTC)
                : null
        );

        return Task
            .builder()
            .id(entity.getId())
            .kind(entity.getKind())
            .project(entity.getProject())
            .metadata(MapUtils.mergeMultipleMaps(meta, metadata.toMap()))
            .spec(converter.convertToEntityAttribute(entity.getSpec()))
            .extra(converter.convertToEntityAttribute(entity.getExtra()))
            .status(
                MapUtils.mergeMultipleMaps(
                    converter.convertToEntityAttribute(entity.getStatus()),
                    Map.of("state", entity.getState().toString())
                )
            )
            .build();
    }

    @Override
    public Task convert(TaskEntity source) {
        return build(source);
    }
}
