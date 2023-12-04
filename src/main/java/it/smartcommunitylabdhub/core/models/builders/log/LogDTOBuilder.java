package it.smartcommunitylabdhub.core.models.builders.log;

import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.converters.types.MetadataConverter;
import it.smartcommunitylabdhub.core.models.entities.log.Log;
import it.smartcommunitylabdhub.core.models.entities.log.LogEntity;
import it.smartcommunitylabdhub.core.models.entities.log.metadata.LogMetadata;
import it.smartcommunitylabdhub.core.utils.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class LogDTOBuilder {

    @Autowired
    MetadataConverter<LogMetadata> metadataConverter;

    public Log build(LogEntity log) {
        return EntityFactory.create(Log::new, log, builder -> builder
                .with(dto -> dto.setId(log.getId()))
                .with(dto -> {
                    // Set Metadata for log
                    LogMetadata logMetadata =
                            Optional.ofNullable(metadataConverter.reverseByClass(
                                    log.getMetadata(),
                                    LogMetadata.class)
                            ).orElseGet(LogMetadata::new);

                    logMetadata.setRun(log.getRun());
                    logMetadata.setVersion(log.getId());
                    logMetadata.setProject(log.getProject());
                    logMetadata.setCreated(log.getCreated());
                    logMetadata.setUpdated(log.getUpdated());
                    dto.setMetadata(logMetadata);
                })
                .with(dto -> dto.setBody(
                        ConversionUtils.reverse(log.getBody(), "cbor")))
                .with(dto -> dto.setExtra(
                        ConversionUtils.reverse(log.getExtra(), "cbor")))
                .with(dto -> dto.setStatus(
                        MapUtils.mergeMultipleMaps(
                                ConversionUtils.reverse(
                                        log.getStatus(),
                                        "cbor"),
                                Map.of("state",
                                        log.getState())
                        )
                ))

        );
    }
}
