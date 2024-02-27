package it.smartcommunitylabdhub.core.models.builders.task;

import it.smartcommunitylabdhub.commons.accessors.fields.StatusFieldAccessor;
import it.smartcommunitylabdhub.commons.exceptions.CoreException;
import it.smartcommunitylabdhub.commons.models.entities.task.Task;
import it.smartcommunitylabdhub.commons.models.entities.task.TaskBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.task.TaskMetadata;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.services.SpecRegistry;
import it.smartcommunitylabdhub.commons.utils.ErrorList;
import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.types.CBORConverter;
import it.smartcommunitylabdhub.core.models.entities.task.TaskEntity;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class TaskEntityBuilder implements Converter<Task, TaskEntity> {

    @Autowired
    CBORConverter cborConverter;

    @Autowired
    SpecRegistry specRegistry;

    /**
     * Build a Task from a TaskDTO and store extra values as a cbor
     * <p>
     *
     * @param dto TaskDTO
     * @return Task the task entity
     */
    public TaskEntity build(Task dto) {
        // Parse and export Spec
        Map<String, Serializable> spec = specRegistry.createSpec(dto.getKind(), EntityName.TASK, dto.getSpec()).toMap();

        // Retrieve Field accessor
        StatusFieldAccessor statusFieldAccessor = StatusFieldAccessor.with(dto.getStatus());
        TaskMetadata metadata = new TaskMetadata();
        metadata.configure(dto.getMetadata());
        TaskBaseSpec taskSpec = new TaskBaseSpec();
        taskSpec.configure(spec);

        return EntityFactory.combine(
            TaskEntity.builder().build(),
            builder ->
                builder
                    // check id
                    .withIf(dto.getId() != null, e -> e.setId(dto.getId()))
                    .with(e -> e.setProject(dto.getProject()))
                    .with(e -> e.setKind(dto.getKind()))
                    .with(r ->
                        r.setFunction(
                            Optional
                                .ofNullable(StringUtils.hasText(taskSpec.getFunction()) ? taskSpec.getFunction() : null)
                                .orElseThrow(() ->
                                    new CoreException(
                                        ErrorList.FUNCTION_NOT_FOUND.getValue(),
                                        ErrorList.FUNCTION_NOT_FOUND.getReason(),
                                        HttpStatus.INTERNAL_SERVER_ERROR
                                    )
                                )
                        )
                    )
                    .with(e -> e.setFunctionId(taskSpec.getFunctionId()))
                    .with(e -> e.setMetadata(cborConverter.convert(dto.getMetadata())))
                    .with(e -> e.setSpec(cborConverter.convert(spec)))
                    .with(e -> e.setStatus(cborConverter.convert(dto.getStatus())))
                    .with(e -> e.setExtra(cborConverter.convert(dto.getExtra())))
                    // Store status if not present
                    .withIfElse(
                        (statusFieldAccessor.getState() == null),
                        (e, condition) -> {
                            if (condition) {
                                e.setState(State.CREATED);
                            } else {
                                e.setState(State.valueOf(statusFieldAccessor.getState()));
                            }
                        }
                    )
                    // Metadata Extraction

                    .withIf(metadata.getCreated() != null, e -> e.setCreated(metadata.getCreated()))
                    .withIf(metadata.getUpdated() != null, e -> e.setUpdated(metadata.getUpdated()))
        );
    }

    @Override
    public TaskEntity convert(Task source) {
        return build(source);
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
            builder ->
                builder
                    .with(e -> e.setFunction(newTask.getFunction()))
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
                    .with(e -> e.setMetadata(newTask.getMetadata()))
                    .with(e -> e.setExtra(newTask.getExtra()))
                    .with(e -> e.setSpec(newTask.getSpec()))
                    .with(e -> e.setStatus(newTask.getStatus()))
        );
    }
}
