package it.smartcommunitylabdhub.core.models.builders.log;

import it.smartcommunitylabdhub.commons.accessors.fields.StatusFieldAccessor;
import it.smartcommunitylabdhub.commons.models.entities.log.Log;
import it.smartcommunitylabdhub.commons.models.entities.log.LogMetadata;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.core.models.converters.types.CBORConverter;
import it.smartcommunitylabdhub.core.models.entities.log.LogEntity;
import java.time.ZoneOffset;
import java.util.Date;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class LogEntityBuilder implements Converter<Log, LogEntity> {

    private final CBORConverter cborConverter;

    public LogEntityBuilder(CBORConverter cborConverter) {
        this.cborConverter = cborConverter;
    }

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

        return LogEntity
            .builder()
            .id(dto.getId())
            .project(dto.getProject())
            .metadata(cborConverter.convert(dto.getMetadata()))
            .body(cborConverter.convert(dto.getBody()))
            .status(cborConverter.convert(dto.getStatus()))
            .extra(cborConverter.convert(dto.getExtra()))
            //extract data
            .run(metadata.getRun())
            .state(
                // Store status if not present
                statusFieldAccessor.getState() == null ? State.CREATED : State.valueOf(statusFieldAccessor.getState())
            )
            // Metadata Extraction

            .created(
                metadata.getCreated() != null
                    ? Date.from(metadata.getCreated().atZone(ZoneOffset.UTC).toInstant())
                    : null
            )
            .updated(
                metadata.getUpdated() != null
                    ? Date.from(metadata.getUpdated().atZone(ZoneOffset.UTC).toInstant())
                    : null
            )
            .build();
    }

    @Override
    public LogEntity convert(Log source) {
        return build(source);
    }
}
