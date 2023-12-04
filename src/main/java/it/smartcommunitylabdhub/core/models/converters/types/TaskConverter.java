package it.smartcommunitylabdhub.core.models.converters.types;

import it.smartcommunitylabdhub.core.annotations.common.ConverterType;
import it.smartcommunitylabdhub.core.exceptions.CustomException;
import it.smartcommunitylabdhub.core.models.converters.interfaces.Converter;
import it.smartcommunitylabdhub.core.models.entities.task.Task;
import it.smartcommunitylabdhub.core.models.entities.task.TaskEntity;

@ConverterType(type = "task")
public class TaskConverter implements Converter<Task, TaskEntity> {

    @Override
    public TaskEntity convert(Task taskDTO) throws CustomException {
        return TaskEntity.builder()
                .id(taskDTO.getId())
                .kind(taskDTO.getKind())
                .project(taskDTO.getProject())
                .build();
    }

    @Override
    public Task reverseConvert(TaskEntity task) throws CustomException {
        return Task.builder()
                .id(task.getId())
                .kind(task.getKind())
                .project(task.getProject())
                .build();
    }

}
