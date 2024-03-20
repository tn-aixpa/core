package it.smartcommunitylabdhub.core.models.builders.task;

import it.smartcommunitylabdhub.commons.accessors.fields.StatusFieldAccessor;
import it.smartcommunitylabdhub.commons.models.entities.task.Task;
import it.smartcommunitylabdhub.commons.models.entities.task.TaskBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.task.TaskMetadata;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.core.models.entities.task.TaskEntity;
import jakarta.persistence.AttributeConverter;
import java.io.Serializable;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TaskEntityBuilder implements Converter<Task, TaskEntity> {

    private final AttributeConverter<Map<String, Serializable>, byte[]> converter;

    public TaskEntityBuilder(
        @Qualifier("cborMapConverter") AttributeConverter<Map<String, Serializable>, byte[]> cborConverter
    ) {
        this.converter = cborConverter;
    }

    /**
     * Build a Task from a TaskDTO and store extra values as a cbor
     * <p>
     *
     * @param dto TaskDTO
     * @return Task the task entity
     */
    public TaskEntity build(Task dto) {
        // Retrieve Field accessor
        StatusFieldAccessor statusFieldAccessor = StatusFieldAccessor.with(dto.getStatus());
        TaskMetadata metadata = new TaskMetadata();
        metadata.configure(dto.getMetadata());

        TaskBaseSpec taskSpec = new TaskBaseSpec();
        taskSpec.configure(dto.getSpec());

        return TaskEntity
            .builder()
            .id(dto.getId())
            .kind(dto.getKind())
            .project(dto.getProject())
            .metadata(converter.convertToDatabaseColumn(dto.getMetadata()))
            .spec(converter.convertToDatabaseColumn(dto.getSpec()))
            .status(converter.convertToDatabaseColumn(dto.getStatus()))
            .extra(converter.convertToDatabaseColumn(dto.getExtra()))
            //extract function
            .function(taskSpec.getFunction())
            .state(
                // Store status if not present
                statusFieldAccessor.getState() == null ? State.CREATED : State.valueOf(statusFieldAccessor.getState())
            )
            // Metadata Extraction
            .created(
                metadata.getCreated() != null
                    ? Date.from(metadata.getCreated().atZoneSameInstant(ZoneOffset.UTC).toInstant())
                    : null
            )
            .updated(
                metadata.getUpdated() != null
                    ? Date.from(metadata.getUpdated().atZoneSameInstant(ZoneOffset.UTC).toInstant())
                    : null
            )
            .build();
    }

    @Override
    public TaskEntity convert(Task source) {
        return build(source);
    }
}
