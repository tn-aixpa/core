package it.smartcommunitylabdhub.core.models.builders.task;

import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.accessors.AccessorRegistry;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecRegistry;
import it.smartcommunitylabdhub.core.exceptions.CoreException;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.Accessor;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.TaskFieldAccessor;
import it.smartcommunitylabdhub.core.models.base.interfaces.Spec;
import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.entities.task.Task;
import it.smartcommunitylabdhub.core.models.entities.task.TaskEntity;
import it.smartcommunitylabdhub.core.models.entities.task.specs.TaskBaseSpec;
import it.smartcommunitylabdhub.core.models.enums.State;
import it.smartcommunitylabdhub.core.utils.ErrorList;
import it.smartcommunitylabdhub.core.utils.JacksonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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


        // Retrieve base spec
        TaskBaseSpec<?> spec = JacksonMapper.objectMapper
                .convertValue(taskDTO.getSpec(), TaskBaseSpec.class);


        return EntityFactory.combine(
                ConversionUtils.convert(taskDTO, "task"), taskDTO,
                builder -> builder
                        // check id
                        .withIfElse(taskDTO.getId() != null &&
                                        taskDTO.getMetadata().getVersion() != null,
                                (t) -> {
                                    if (taskDTO.getId()
                                            .equals(taskDTO.getMetadata().getVersion())) {
                                        t.setId(taskDTO.getMetadata().getVersion());
                                    } else {
                                        throw new CoreException(
                                                ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                                                "Trying to store item with which has different signature <id != version>",
                                                HttpStatus.INTERNAL_SERVER_ERROR
                                        );
                                    }
                                },
                                (t) -> {
                                    if (taskDTO.getId() == null &&
                                            taskDTO.getMetadata().getVersion() != null) {
                                        t.setId(taskDTO.getMetadata().getVersion());
                                    } else {
                                        t.setId(taskDTO.getId());
                                    }
                                })
                        .with(t -> t.setFunction(spec.getFunction()))
                        .withIfElse(taskFieldAccessor.getState().equals(State.NONE.name()),
                                (r, condition) -> {
                                    if (condition) {
                                        r.setState(State.CREATED);
                                    } else {
                                        r.setState(State.valueOf(taskFieldAccessor.getState()));
                                    }
                                }
                        )
                        .with(t -> t.setMetadata(ConversionUtils.convert(
                                taskDTO.getMetadata(), "metadata")))
                        .with(t -> t.setExtra(ConversionUtils.convert(
                                taskDTO.getExtra(), "cbor")))
                        .with(t -> t.setSpec(ConversionUtils.convert(
                                spec.toMap(), "cbor")))
                        .with(t -> t.setStatus(ConversionUtils.convert(
                                taskDTO.getStatus(), "cbor")))
                        .withIf(taskDTO.getMetadata().getCreated() != null, (t) ->
                                t.setCreated(taskDTO.getMetadata().getCreated()))
                        .withIf(taskDTO.getMetadata().getUpdated() != null, (t) ->
                                t.setUpdated(taskDTO.getMetadata().getUpdated())
                        )
        );
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
                                (r, condition) -> {
                                    if (condition) {
                                        r.setState(State.CREATED);
                                    } else {
                                        r.setState(State.valueOf(taskFieldAccessor.getState()));
                                    }
                                }
                        )
                        .with(t -> t.setMetadata(ConversionUtils.convert(
                                taskDTO.getMetadata(), "metadata")))
                        .with(t -> t.setExtra(ConversionUtils.convert(
                                taskDTO.getExtra(), "cbor")))
                        .with(t -> t.setSpec(ConversionUtils.convert(
                                spec.toMap(), "cbor")))
                        .with(t -> t.setStatus(ConversionUtils.convert(
                                taskDTO.getStatus(), "cbor")))
        );
    }
}
