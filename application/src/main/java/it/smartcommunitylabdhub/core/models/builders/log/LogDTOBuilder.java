package it.smartcommunitylabdhub.core.models.builders.log;

import it.smartcommunitylabdhub.commons.models.entities.log.Log;
import it.smartcommunitylabdhub.commons.models.entities.log.LogMetadata;
import it.smartcommunitylabdhub.commons.utils.MapUtils;
import it.smartcommunitylabdhub.core.models.entities.LogEntity;
import it.smartcommunitylabdhub.core.models.metadata.AuditMetadataBuilder;
import it.smartcommunitylabdhub.core.models.metadata.BaseMetadataBuilder;
import jakarta.persistence.AttributeConverter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class LogDTOBuilder implements Converter<LogEntity, Log> {

    private final AttributeConverter<Map<String, Serializable>, byte[]> converter;
    private BaseMetadataBuilder baseMetadataBuilder;
    private AuditMetadataBuilder auditingMetadataBuilder;

    public LogDTOBuilder(
        @Qualifier("cborMapConverter") AttributeConverter<Map<String, Serializable>, byte[]> cborConverter
    ) {
        this.converter = cborConverter;
    }

    @Autowired
    public void setBaseMetadataBuilder(BaseMetadataBuilder baseMetadataBuilder) {
        this.baseMetadataBuilder = baseMetadataBuilder;
    }

    @Autowired
    public void setAuditingMetadataBuilder(AuditMetadataBuilder auditingMetadataBuilder) {
        this.auditingMetadataBuilder = auditingMetadataBuilder;
    }

    public Log build(LogEntity entity) {
        //read metadata as-is
        Map<String, Serializable> meta = converter.convertToEntityAttribute(entity.getMetadata());

        // build metadata
        Map<String, Serializable> metadata = new HashMap<>();
        metadata.putAll(meta);

        LogMetadata logMetadata = LogMetadata.from(meta);
        logMetadata.setRun(entity.getRun());
        metadata.putAll(logMetadata.toMap());

        Optional.of(baseMetadataBuilder.convert(entity)).ifPresent(m -> metadata.putAll(m.toMap()));
        Optional.of(auditingMetadataBuilder.convert(entity)).ifPresent(m -> metadata.putAll(m.toMap()));

        return Log
            .builder()
            .id(entity.getId())
            .project(entity.getProject())
            .user(entity.getCreatedBy())
            .metadata(metadata)
            .body(converter.convertToEntityAttribute(entity.getBody()))
            .extra(converter.convertToEntityAttribute(entity.getExtra()))
            .status(
                MapUtils.mergeMultipleMaps(
                    converter.convertToEntityAttribute(entity.getStatus()),
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
