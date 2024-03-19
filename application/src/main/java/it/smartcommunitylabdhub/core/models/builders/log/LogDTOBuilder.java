package it.smartcommunitylabdhub.core.models.builders.log;

import it.smartcommunitylabdhub.commons.models.entities.log.Log;
import it.smartcommunitylabdhub.commons.models.entities.log.LogMetadata;
import it.smartcommunitylabdhub.commons.utils.MapUtils;
import it.smartcommunitylabdhub.core.models.converters.types.CBORConverter;
import it.smartcommunitylabdhub.core.models.entities.log.LogEntity;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class LogDTOBuilder implements Converter<LogEntity, Log> {

    private final CBORConverter cborConverter;

    public LogDTOBuilder(CBORConverter cborConverter) {
        this.cborConverter = cborConverter;
    }

    public Log build(LogEntity entity) {
        //read metadata as-is
        Map<String, Serializable> meta = cborConverter.reverseConvert(entity.getMetadata());

        // Set Metadata for log
        LogMetadata metadata = new LogMetadata();
        metadata.configure(meta);

        metadata.setRun(entity.getRun());
        metadata.setProject(entity.getProject());
        metadata.setCreated(
            entity.getCreated() != null
                ? LocalDateTime.ofInstant(entity.getCreated().toInstant(), ZoneOffset.UTC)
                : null
        );
        metadata.setUpdated(
            entity.getUpdated() != null
                ? LocalDateTime.ofInstant(entity.getUpdated().toInstant(), ZoneOffset.UTC)
                : null
        );

        return Log
            .builder()
            .id(entity.getId())
            .project(entity.getProject())
            .metadata(MapUtils.mergeMultipleMaps(meta, metadata.toMap()))
            .body(cborConverter.reverseConvert(entity.getBody()))
            .extra(cborConverter.reverseConvert(entity.getExtra()))
            .status(
                MapUtils.mergeMultipleMaps(
                    cborConverter.reverseConvert(entity.getStatus()),
                    Map.of("state", entity.getState().toString())
                )
            )
            .build();
    }

    @Override
    public Log convert(LogEntity source) {
        return build(source);
    }
}
