package it.smartcommunitylabdhub.core.models.builders.log;

import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.accessors.AccessorRegistry;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.Accessor;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.LogFieldAccessor;
import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.entities.log.Log;
import it.smartcommunitylabdhub.core.models.entities.log.LogEntity;
import it.smartcommunitylabdhub.core.models.enums.State;
import it.smartcommunitylabdhub.core.utils.jackson.JacksonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LogEntityBuilder {


    @Autowired
    AccessorRegistry<? extends Accessor<Object>> accessorRegistry;


    /**
     * Build a Log from a LogDTO and store extra values as a cbor
     *
     * @return LogEntity
     */
    public LogEntity build(Log logDTO) {

        // Retrieve field accessor
        LogFieldAccessor<?> logFieldAccessor =
                accessorRegistry.createAccessor(
                        "log",
                        EntityName.LOG,
                        JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(logDTO,
                                JacksonMapper.typeRef));

        return EntityFactory.combine(
                LogEntity.builder().build(), logDTO,
                builder -> builder
                        // check id
                        .withIf(logDTO.getId() != null,
                                (l) -> l.setId(logDTO.getId()))
                        .with(l -> l.setMetadata(ConversionUtils.convert(
                                logDTO.getMetadata(), "metadata")))
                        .with(l -> l.setExtra(ConversionUtils.convert(
                                logDTO.getExtra(), "cbor")))
                        .with(l -> l.setBody(ConversionUtils.convert(
                                logDTO.getBody(), "cbor")))
                        .with(l -> l.setStatus(ConversionUtils.convert(
                                logDTO.getStatus(), "cbor")))

                        // Store status if not present
                        .withIfElse(logFieldAccessor.getState().equals(State.NONE.name()),
                                (l, condition) -> {
                                    if (condition) {
                                        l.setState(State.CREATED);
                                    } else {
                                        l.setState(State.valueOf(logFieldAccessor.getState()));
                                    }
                                }
                        )
                        .withIf(logDTO.getMetadata().getRun() != null, (l) ->
                                l.setRun(logDTO.getMetadata().getRun()))
                        .withIf(logDTO.getMetadata().getProject() != null, (l) ->
                                l.setProject(logDTO.getMetadata().getProject()))
                        .withIf(logDTO.getMetadata().getCreated() != null, (l) ->
                                l.setCreated(logDTO.getMetadata().getCreated()))
                        .withIf(logDTO.getMetadata().getUpdated() != null, (l) ->
                                l.setUpdated(logDTO.getMetadata().getUpdated()))
        );
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
                log, newLog, builder -> builder
                        .with(l -> l.setMetadata(newLog.getMetadata()))
                        .with(l -> l.setExtra(newLog.getExtra()))
                        .with(l -> l.setStatus(newLog.getStatus()))
                        .with(l -> l.setBody(newLog.getBody()))

                        // Store status if not present
                        .withIfElse(newLog.getState().name().equals(State.NONE.name()),
                                (l, condition) -> {
                                    if (condition) {
                                        l.setState(State.CREATED);
                                    } else {
                                        l.setState(newLog.getState());
                                    }
                                }
                        )
                        .withIf(newLog.getRun() != null, (l) ->
                                l.setRun(newLog.getRun()))
                        .withIf(newLog.getProject() != null, (l) ->
                                l.setProject(newLog.getProject()))
                        .withIf(newLog.getCreated() != null, (l) ->
                                l.setCreated(newLog.getCreated()))
                        .withIf(newLog.getUpdated() != null, (l) ->
                                l.setUpdated(newLog.getUpdated()))

        );
    }
}
