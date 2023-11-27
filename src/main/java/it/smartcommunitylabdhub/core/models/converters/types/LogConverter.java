package it.smartcommunitylabdhub.core.models.converters.types;

import it.smartcommunitylabdhub.core.annotations.common.ConverterType;
import it.smartcommunitylabdhub.core.exceptions.CustomException;
import it.smartcommunitylabdhub.core.models.converters.interfaces.Converter;
import it.smartcommunitylabdhub.core.models.entities.log.Log;
import it.smartcommunitylabdhub.core.models.entities.log.LogEntity;
import it.smartcommunitylabdhub.core.models.enums.State;

@ConverterType(type = "log")
public class LogConverter implements Converter<Log, LogEntity> {

    @Override
    public LogEntity convert(Log logDTO) throws CustomException {
        return LogEntity.builder()
                .id(logDTO.getId())
                .project(logDTO.getProject())
                .run(logDTO.getRun())
                .state(logDTO.getState() == null ? State.CREATED : State.valueOf(logDTO.getState()))
                .build();
    }

    @Override
    public Log reverseConvert(LogEntity log) throws CustomException {
        return Log.builder()
                .id(log.getId())
                .project(log.getProject())
                .run(log.getRun())
                .state(log.getState() == null ? State.CREATED.name() : log.getState().name())
                .created(log.getCreated())
                .updated(log.getUpdated())
                .build();
    }

}
