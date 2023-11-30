package it.smartcommunitylabdhub.core.models.builders.task;

import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.accessors.AccessorRegistry;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecRegistry;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.Accessor;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.TaskFieldAccessor;
import it.smartcommunitylabdhub.core.models.base.interfaces.Spec;
import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.entities.task.Task;
import it.smartcommunitylabdhub.core.models.entities.task.TaskEntity;
import it.smartcommunitylabdhub.core.models.entities.task.specs.TaskBaseSpec;
import it.smartcommunitylabdhub.core.models.enums.State;
import it.smartcommunitylabdhub.core.utils.JacksonMapper;
import it.smartcommunitylabdhub.core.utils.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TaskEntityBuilder {

    @Autowired
    SpecRegistry<? extends Spec> specRegistry;

    @Autowired
    AccessorRegistry<? extends Accessor<Object>> accessorRegistry;

    /**
     * Build a Task from a TaskDTO and store extra values as a cbor
     * <p>
     *
     * @param taskDTO TaskDTO
     * @return Task the task entity
     */
    public TaskEntity build(Task taskDTO) {

        // Validate Spec
        specRegistry.createSpec(taskDTO.getKind(), EntityName.TASK, Map.of());

        // Retrieve Field accessor
        TaskFieldAccessor<?> taskFieldAccessor =
                accessorRegistry.createAccessor(
                        taskDTO.getKind(),
                        EntityName.TASK,
                        JacksonMapper.objectMapper.convertValue(taskDTO,
                                JacksonMapper.typeRef));

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
                        .withIfElse(taskFieldAccessor.getState().equals(State.NONE.name()),
                                (dto, condition) -> {
                                    if (condition) {
                                        dto.setStatus(ConversionUtils.convert(
                                                MapUtils.mergeMultipleMaps(
                                                        taskFieldAccessor.getStatus(),
                                                        Map.of("state", State.CREATED.name())
                                                ), "cbor")
                                        );
                                        dto.setState(State.CREATED);
                                    } else {
                                        dto.setStatus(
                                                ConversionUtils.convert(
                                                        taskFieldAccessor.getStatus(),
                                                        "cbor")
                                        );
                                        dto.setState(State.valueOf(taskFieldAccessor.getState()));
                                    }
                                }
                        )
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
     * @param task    Task
     * @param taskDTO TaskDTO
     * @return TaskEntity
     */
    public TaskEntity update(TaskEntity task, Task taskDTO) {

        // Validate Spec
        specRegistry.createSpec(taskDTO.getKind(), EntityName.TASK, Map.of());

        // Retrieve Field accessor
        TaskFieldAccessor<?> taskFieldAccessor =
                accessorRegistry.createAccessor(
                        taskDTO.getKind(),
                        EntityName.TASK,
                        JacksonMapper.objectMapper.convertValue(taskDTO,
                                JacksonMapper.typeRef));

        // Retrieve base spec
        TaskBaseSpec<?> spec = JacksonMapper.objectMapper
                .convertValue(taskDTO.getSpec(), TaskBaseSpec.class);

        return EntityFactory.combine(
                task, taskDTO, builder -> builder
                        .with(t -> t.setFunction(spec.getFunction()))
                        .withIfElse(taskFieldAccessor.getState().equals(State.NONE.name()),
                                (dto, condition) -> {
                                    if (condition) {
                                        dto.setStatus(ConversionUtils.convert(
                                                MapUtils.mergeMultipleMaps(
                                                        taskFieldAccessor.getStatus(),
                                                        Map.of("state", State.CREATED.name())
                                                ), "cbor")
                                        );
                                        dto.setState(State.CREATED);
                                    } else {
                                        dto.setStatus(
                                                ConversionUtils.convert(
                                                        taskFieldAccessor.getStatus(),
                                                        "cbor")
                                        );
                                        dto.setState(State.valueOf(taskFieldAccessor.getState()));
                                    }
                                }
                        )
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
