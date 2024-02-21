package it.smartcommunitylabdhub.core.models.builders.log;

import it.smartcommunitylabdhub.commons.accessors.fields.StatusFieldAccessor;
import it.smartcommunitylabdhub.commons.models.entities.log.Log;
import it.smartcommunitylabdhub.commons.models.entities.log.LogMetadata;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.types.CBORConverter;
import it.smartcommunitylabdhub.core.models.entities.log.LogEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class LogEntityBuilder implements Converter<Log, LogEntity> {

    @Autowired
    CBORConverter cborConverter;

    /**
     * Build a Log from a LogDTO and store extra values as a cbor
     *
     * @return LogEntity
     */
    public LogEntity build(Log dto) {
        // Retrieve field accessor
        StatusFieldAccessor statusFieldAccessor = StatusFieldAccessor.with(dto.getStatus());
        LogMetadata metadata = new LogMetadata();
        metadata.configure(dto.getMetadata());

        return EntityFactory.combine(
                LogEntity.builder().build(),
                builder ->
                        builder
                                // check id
                                .withIf(dto.getId() != null, e -> e.setId(dto.getId()))
                                .with(e -> e.setMetadata(cborConverter.convert(dto.getMetadata())))
                                .with(e -> e.setBody(cborConverter.convert(dto.getBody())))
                                .with(e -> e.setStatus(cborConverter.convert(dto.getStatus())))
                                .with(e -> e.setExtra(cborConverter.convert(dto.getExtra())))
                                // Store status if not present
                                .withIfElse(
                                        (statusFieldAccessor.getState() == null),
                                        (l, condition) -> {
                                            if (condition) {
                                                l.setState(State.CREATED);
                                            } else {
                                                l.setState(State.valueOf(statusFieldAccessor.getState()));
                                            }
                                        }
                                )
                                // Metadata Extraction

                                .withIf(metadata.getCreated() != null, e -> e.setCreated(metadata.getCreated()))
                                .withIf(metadata.getUpdated() != null, e -> e.setUpdated(metadata.getUpdated()))
                                .withIf(metadata.getRun() != null, e -> e.setRun(metadata.getRun()))
                                .withIf(metadata.getProject() != null, e -> e.setProject(metadata.getProject()))
        );
    }

    @Override
    public LogEntity convert(Log source) {
        return build(source);
    }

    /**
     * Update a Log if element is not passed it override causing empty field
     *
     * @param log Log
     * @return LogEntity
     */
    public LogEntity update(LogEntity log, Log logDTO) {
        LogEntity newLog = build(logDTO);

        return doUpdate(log, newLog);
    }

    /**
     * Update a Log if element is not passed it override causing empty field
     *
     * @param log Log
     * @return LogEntity
     */
    public LogEntity doUpdate(LogEntity log, LogEntity newLog) {
        return EntityFactory.combine(
                log,
                builder ->
                        builder
                                .with(e -> e.setMetadata(newLog.getMetadata()))
                                .with(e -> e.setExtra(newLog.getExtra()))
                                .with(e -> e.setStatus(newLog.getStatus()))
                                .with(e -> e.setBody(newLog.getBody()))
                                // Store status if not present
                                .withIfElse(
                                        newLog.getState().name().equals(State.NONE.name()),
                                        (l, condition) -> {
                                            if (condition) {
                                                l.setState(State.CREATED);
                                            } else {
                                                l.setState(newLog.getState());
                                            }
                                        }
                                )
                                .withIf(newLog.getRun() != null, e -> e.setRun(newLog.getRun()))
                                .withIf(newLog.getProject() != null, e -> e.setProject(newLog.getProject()))
                                .withIf(newLog.getCreated() != null, e -> e.setCreated(newLog.getCreated()))
                                .withIf(newLog.getUpdated() != null, e -> e.setUpdated(newLog.getUpdated()))
        );
    }
}
