package it.smartcommunitylabdhub.core.models.builders.log;

import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.accessors.AccessorRegistry;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.Accessor;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.FunctionFieldAccessor;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.LogFieldAccessor;
import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.entities.log.Log;
import it.smartcommunitylabdhub.core.models.entities.log.LogEntity;
import it.smartcommunitylabdhub.core.models.enums.State;
import it.smartcommunitylabdhub.core.utils.JacksonMapper;
import it.smartcommunitylabdhub.core.utils.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class LogEntityBuilder {

    @Autowired
    AccessorRegistry<? extends Accessor<Object>> accessorRegistry;


    /**
     * Build a Log from a LogDTO and store extra values as a cbor
     *
     * @return
     */
    public LogEntity build(Log logDTO) {

        // Retrieve field accessor
        FunctionFieldAccessor<?> functionFieldAccessor =
                accessorRegistry.createAccessor(
                        "log",
                        EntityName.LOG,
                        JacksonMapper.objectMapper.convertValue(logDTO,
                                JacksonMapper.typeRef));

        return EntityFactory.combine(
                ConversionUtils.convert(logDTO, "log"), logDTO,
                builder -> builder
                        .with(f -> f.setExtra(
                                ConversionUtils.convert(
                                        logDTO.getExtra(),
                                        "cbor")))
                        .with(f -> f.setBody(
                                ConversionUtils.convert(
                                        logDTO.getBody(),
                                        "cbor"))));
    }

    /**
     * Update a Log if element is not passed it override causing empty field
     *
     * @param log
     * @return
     */
    public LogEntity update(LogEntity log, Log logDTO) {

        LogFieldAccessor<?> logFieldAccessor =
                accessorRegistry.createAccessor(
                        "log",
                        EntityName.LOG,
                        JacksonMapper.objectMapper.convertValue(logDTO,
                                JacksonMapper.typeRef));

        return EntityFactory.combine(
                log, logDTO, builder -> builder
                        .with(f -> f.setRun(logDTO.getRun()))
                        .with(f -> f.setProject(logDTO.getProject()))
                        .withIfElse(logFieldAccessor.getState().equals(State.NONE.name()),
                                (dto, condition) -> {
                                    if (condition) {
                                        dto.setStatus(ConversionUtils.convert(
                                                MapUtils.mergeMultipleMaps(
                                                        logFieldAccessor.getStatus(),
                                                        Map.of("state", State.CREATED.name())
                                                ), "cbor")
                                        );
                                        dto.setState(State.CREATED);
                                    } else {
                                        dto.setStatus(
                                                ConversionUtils.convert(
                                                        logFieldAccessor.getStatus(),
                                                        "cbor")
                                        );
                                        dto.setState(State.valueOf(logFieldAccessor.getState()));
                                    }
                                }
                        )
                        .with(f -> f.setExtra(
                                ConversionUtils.convert(
                                        logDTO.getExtra(),

                                        "cbor")))
                        .with(f -> f.setBody(
                                ConversionUtils.convert(
                                        logDTO.getBody(),

                                        "cbor"))));
    }
}
