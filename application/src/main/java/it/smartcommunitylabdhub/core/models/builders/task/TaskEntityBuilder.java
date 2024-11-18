package it.smartcommunitylabdhub.core.models.builders.task;

import it.smartcommunitylabdhub.commons.models.entities.function.FunctionTaskBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.task.Task;
import it.smartcommunitylabdhub.commons.models.entities.workflow.WorkflowTaskBaseSpec;
import it.smartcommunitylabdhub.commons.models.metadata.BaseMetadata;
import it.smartcommunitylabdhub.core.models.entities.TaskEntity;
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
        // Extract data
        BaseMetadata metadata = BaseMetadata.from(dto.getMetadata());

        return TaskEntity
            .builder()
            .id(dto.getId())
            .kind(dto.getKind())
            .project(dto.getProject())
            .metadata(converter.convertToDatabaseColumn(dto.getMetadata()))
            .spec(converter.convertToDatabaseColumn(dto.getSpec()))
            //extract refs from specs
            .function(FunctionTaskBaseSpec.from(dto.getSpec()).getFunction())
            .workflow(WorkflowTaskBaseSpec.from(dto.getSpec()).getWorkflow())
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
