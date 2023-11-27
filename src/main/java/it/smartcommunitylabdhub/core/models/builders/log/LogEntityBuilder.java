package it.smartcommunitylabdhub.core.models.builders.log;

import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.entities.log.Log;
import it.smartcommunitylabdhub.core.models.entities.log.LogEntity;
import it.smartcommunitylabdhub.core.models.enums.State;
import org.springframework.stereotype.Component;

@Component
public class LogEntityBuilder {

    /**
     * Build a Log from a LogDTO and store extra values as a cbor
     *
     * @return
     */
    public LogEntity build(Log logDTO) {
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
        return EntityFactory.combine(
                log, logDTO, builder -> builder
                        .with(f -> f.setRun(logDTO.getRun()))
                        .with(f -> f.setProject(logDTO.getProject()))
                        .with(f -> f.setState(logDTO.getState() == null
                                ? State.CREATED
                                : State.valueOf(logDTO.getState())))
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
