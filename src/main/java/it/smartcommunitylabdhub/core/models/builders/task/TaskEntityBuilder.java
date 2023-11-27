package it.smartcommunitylabdhub.core.models.builders.task;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecEntity;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecRegistry;
import it.smartcommunitylabdhub.core.models.base.interfaces.Spec;
import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.entities.task.Task;
import it.smartcommunitylabdhub.core.models.entities.task.TaskEntity;
import it.smartcommunitylabdhub.core.models.entities.task.specs.TaskBaseSpec;
import it.smartcommunitylabdhub.core.models.enums.State;
import it.smartcommunitylabdhub.core.utils.JacksonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TaskEntityBuilder {


    @Autowired
    SpecRegistry<? extends Spec> specRegistry;

    /**
     * Build a Task from a TaskDTO and store extra values as a cbor
     *
     * @param taskDTO
     * @return Task
     */
    public TaskEntity build(Task taskDTO) {

        specRegistry.createSpec(taskDTO.getKind(), SpecEntity.TASK, Map.of());

        // Retrieve the task
        TaskEntity task = ConversionUtils.convert(taskDTO, "task");

        // Retrieve base spec
        TaskBaseSpec<?> spec = JacksonMapper.objectMapper
                .convertValue(taskDTO.getSpec(), TaskBaseSpec.class);

        // Merge function
        task.setFunction(spec.getFunction());

        return EntityFactory.combine(
                task, taskDTO,
                builder -> builder
                        .with(t -> t.setMetadata(
                                ConversionUtils.convert(taskDTO
                                                .getMetadata(),
                                        "metadata")))

                        .with(t -> t.setExtra(
                                ConversionUtils.convert(
                                        taskDTO.getExtra(),
                                        "cbor")))
                        .with(t -> t.setSpec(
                                ConversionUtils.convert(
                                        spec.toMap(),
                                        "cbor"))));
    }

    /**
     * Update a Task if element is not passed it override causing empty field
     *
     * @param task
     * @param taskDTO
     * @return
     */
    public TaskEntity update(TaskEntity task, Task taskDTO) {
        // Retrieve base spec
        TaskBaseSpec spec = JacksonMapper.objectMapper
                .convertValue(taskDTO.getSpec(), TaskBaseSpec.class);

        return EntityFactory.combine(
                task, taskDTO, builder -> builder
                        .with(t -> t.setFunction(spec.getFunction()))
                        .with(t -> t.setState(taskDTO.getState() == null
                                ? State.CREATED
                                : State.valueOf(taskDTO
                                .getState())))
                        .with(t -> t.setMetadata(
                                ConversionUtils.convert(taskDTO
                                                .getMetadata(),
                                        "metadata")))

                        .with(t -> t.setExtra(
                                ConversionUtils.convert(
                                        taskDTO.getExtra(),

                                        "cbor")))
                        .with(t -> t.setSpec(
                                ConversionUtils.convert(spec.toMap(),
                                        "cbor"))));
    }
}
