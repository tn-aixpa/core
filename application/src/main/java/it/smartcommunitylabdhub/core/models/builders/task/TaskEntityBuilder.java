package it.smartcommunitylabdhub.core.models.builders.task;

import it.smartcommunitylabdhub.commons.accessors.fields.StatusFieldAccessor;
import it.smartcommunitylabdhub.commons.models.entities.task.Task;
import it.smartcommunitylabdhub.commons.models.entities.task.TaskBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.task.TaskMetadata;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.core.models.converters.types.CBORConverter;
import it.smartcommunitylabdhub.core.models.entities.task.TaskEntity;
import java.time.ZoneOffset;
import java.util.Date;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TaskEntityBuilder implements Converter<Task, TaskEntity> {

    private final CBORConverter cborConverter;

    public TaskEntityBuilder(CBORConverter cborConverter) {
        this.cborConverter = cborConverter;
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
            .metadata(cborConverter.convert(dto.getMetadata()))
            .spec(cborConverter.convert(dto.getSpec()))
            .status(cborConverter.convert(dto.getStatus()))
            .extra(cborConverter.convert(dto.getExtra()))
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
