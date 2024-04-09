package it.smartcommunitylabdhub.core.models.builders.log;

import it.smartcommunitylabdhub.commons.accessors.fields.StatusFieldAccessor;
import it.smartcommunitylabdhub.commons.models.entities.log.Log;
import it.smartcommunitylabdhub.commons.models.entities.log.LogMetadata;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.core.models.entities.LogEntity;
import jakarta.persistence.AttributeConverter;
import java.io.Serializable;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class LogEntityBuilder implements Converter<Log, LogEntity> {

    private final AttributeConverter<Map<String, Serializable>, byte[]> converter;

    public LogEntityBuilder(
        @Qualifier("cborMapConverter") AttributeConverter<Map<String, Serializable>, byte[]> cborConverter
    ) {
        this.converter = cborConverter;
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
            .metadata(converter.convertToDatabaseColumn(dto.getMetadata()))
            .body(converter.convertToDatabaseColumn(dto.getBody()))
            .status(converter.convertToDatabaseColumn(dto.getStatus()))
            .extra(converter.convertToDatabaseColumn(dto.getExtra()))
            //extract data
            .run(metadata.getRun())
            .state(
                // Store status if not present
                statusFieldAccessor.getState() == null ? State.CREATED : State.valueOf(statusFieldAccessor.getState())
            )
            // Metadata Extraction
            .created(
                metadata.getCreated() != null
                    ? Date.from(metadata.getCreated().atZoneSameInstant(ZoneOffset.UTC).toInstant())
                    : null
            )
            .updated(
                metadata.getUpdated() != null
                    ? Date.from(metadata.getUpdated().atZoneSameInstant(ZoneOffset.UTC).toInstant())
                    : null
            )
            .build();
    }

    @Override
    public LogEntity convert(Log source) {
        return build(source);
    }
}
