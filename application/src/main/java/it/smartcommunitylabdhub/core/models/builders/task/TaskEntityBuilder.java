package it.smartcommunitylabdhub.core.models.builders.task;

import it.smartcommunitylabdhub.commons.exceptions.CoreException;
import it.smartcommunitylabdhub.commons.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.commons.infrastructure.factories.specs.SpecRegistry;
import it.smartcommunitylabdhub.commons.models.accessors.fields.TaskFieldAccessor;
import it.smartcommunitylabdhub.commons.models.entities.task.Task;
import it.smartcommunitylabdhub.commons.models.entities.task.specs.TaskBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.utils.ErrorList;
import it.smartcommunitylabdhub.commons.utils.jackson.JacksonMapper;
import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.entities.task.TaskEntity;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class TaskEntityBuilder {

    @Autowired
    SpecRegistry specRegistry;

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
        TaskFieldAccessor taskFieldAccessor = TaskFieldAccessor.with(
            JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(taskDTO, JacksonMapper.typeRef)
        );

        // Retrieve base spec
        TaskBaseSpec spec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(taskDTO.getSpec(), TaskBaseSpec.class);

        return EntityFactory.combine(
            TaskEntity.builder().build(),
            taskDTO,
            builder ->
                builder
                    // check id
                    .withIf(taskDTO.getId() != null, t -> t.setId(taskDTO.getId()))
                    .with(t -> t.setProject(taskDTO.getProject()))
                    .with(t -> t.setKind(taskDTO.getKind()))
                    .with(r ->
                        r.setFunction(
                            Optional
                                .ofNullable(StringUtils.hasText(spec.getFunction()) ? spec.getFunction() : null)
                                .orElseThrow(() ->
                                    new CoreException(
                                        ErrorList.FUNCTION_NOT_FOUND.getValue(),
                                        ErrorList.FUNCTION_NOT_FOUND.getReason(),
                                        HttpStatus.INTERNAL_SERVER_ERROR
                                    )
                                )
                        )
                    )
                    .withIfElse(
                        taskFieldAccessor.getState().equals(State.NONE.name()),
                        (r, condition) -> {
                            if (condition) {
                                r.setState(State.CREATED);
                            } else {
                                r.setState(State.valueOf(taskFieldAccessor.getState()));
                            }
                        }
                    )
                    .with(t -> t.setMetadata(ConversionUtils.convert(taskDTO.getMetadata(), "metadata")))
                    .with(t -> t.setExtra(ConversionUtils.convert(taskDTO.getExtra(), "cbor")))
                    .with(t -> t.setSpec(ConversionUtils.convert(spec.toMap(), "cbor")))
                    .with(t -> t.setStatus(ConversionUtils.convert(taskDTO.getStatus(), "cbor")))
                    .withIf(
                        taskDTO.getMetadata().getCreated() != null,
                        t -> t.setCreated(taskDTO.getMetadata().getCreated())
                    )
                    .withIf(
                        taskDTO.getMetadata().getUpdated() != null,
                        t -> t.setUpdated(taskDTO.getMetadata().getUpdated())
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
        TaskEntity newTask = build(taskDTO);
        return doUpdate(task, newTask);
    }

    /**
     * Updates a TaskEntity with the values from a newTask object.
     *
     * @param task    the original TaskEntity to be updated
     * @param newTask the TaskEntity containing the updated values
     * @return the updated TaskEntity
     */
    public TaskEntity doUpdate(TaskEntity task, TaskEntity newTask) {
        return EntityFactory.combine(
            task,
            newTask,
            builder ->
                builder
                    .with(t -> t.setFunction(newTask.getFunction()))
                    .withIfElse(
                        newTask.getState().name().equals(State.NONE.name()),
                        (r, condition) -> {
                            if (condition) {
                                r.setState(State.CREATED);
                            } else {
                                r.setState(newTask.getState());
                            }
                        }
                    )
                    .with(t -> t.setMetadata(newTask.getMetadata()))
                    .with(t -> t.setExtra(newTask.getExtra()))
                    .with(t -> t.setSpec(newTask.getSpec()))
                    .with(t -> t.setStatus(newTask.getStatus()))
        );
    }
}
