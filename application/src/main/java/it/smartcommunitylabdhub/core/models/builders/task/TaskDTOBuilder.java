package it.smartcommunitylabdhub.core.models.builders.task;

import it.smartcommunitylabdhub.commons.models.entities.task.Task;
import it.smartcommunitylabdhub.commons.models.entities.task.TaskMetadata;
import it.smartcommunitylabdhub.commons.utils.MapUtils;
import it.smartcommunitylabdhub.core.models.converters.types.CBORConverter;
import it.smartcommunitylabdhub.core.models.entities.task.TaskEntity;
import java.io.Serializable;
import java.util.Map;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class TaskDTOBuilder implements Converter<TaskEntity, Task> {

    private final CBORConverter cborConverter;

    public TaskDTOBuilder(CBORConverter cborConverter) {
        this.cborConverter = cborConverter;
    }

    /**
     * Build a taskDTO given a task
     *
     * @param entity the Task
     * @return TaskDTO
     */
    public Task build(TaskEntity entity, boolean embeddable) {
        //read metadata map as-is
        Map<String, Serializable> meta = cborConverter.reverseConvert(entity.getMetadata());

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
        metadata.setCreated(entity.getCreated());
        metadata.setUpdated(entity.getUpdated());

        return Task
            .builder()
            .id(entity.getId())
            .kind(entity.getKind())
            .project(entity.getProject())
            .metadata(MapUtils.mergeMultipleMaps(meta, metadata.toMap()))
            .spec(embeddable ? null : cborConverter.reverseConvert(entity.getSpec()))
            .extra(embeddable ? null : cborConverter.reverseConvert(entity.getExtra()))
            .status(
                embeddable
                    ? null
                    : MapUtils.mergeMultipleMaps(
                        cborConverter.reverseConvert(entity.getStatus()),
                        Map.of("state", entity.getState().toString())
                    )
            )
            .build();
    }

    @Override
    public Task convert(TaskEntity source) {
        return build(source, false);
    }
}
