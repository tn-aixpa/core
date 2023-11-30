package it.smartcommunitylabdhub.core.models.converters.types;

import it.smartcommunitylabdhub.core.annotations.common.ConverterType;
import it.smartcommunitylabdhub.core.exceptions.CustomException;
import it.smartcommunitylabdhub.core.models.converters.interfaces.Converter;
import it.smartcommunitylabdhub.core.models.entities.log.Log;
import it.smartcommunitylabdhub.core.models.entities.log.LogEntity;

@ConverterType(type = "log")
public class LogConverter implements Converter<Log, LogEntity> {

    @Override
    public LogEntity convert(Log logDTO) throws CustomException {
        return LogEntity.builder()
                .id(logDTO.getId())
                .project(logDTO.getProject())
                .run(logDTO.getRun())
                .build();
    }

    @Override
    public Log reverseConvert(LogEntity log) throws CustomException {
        return Log.builder()
                .id(log.getId())
                .project(log.getProject())
                .run(log.getRun())
                .created(log.getCreated())
                .updated(log.getUpdated())
                .build();
    }

}
