package it.smartcommunitylabdhub.core.models.builders.run;

import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.entities.run.RunMetadata;
import it.smartcommunitylabdhub.commons.utils.MapUtils;
import it.smartcommunitylabdhub.core.models.entities.RunEntity;
import jakarta.persistence.AttributeConverter;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class RunDTOBuilder implements Converter<RunEntity, Run> {

    private final AttributeConverter<Map<String, Serializable>, byte[]> converter;

    public RunDTOBuilder(
        @Qualifier("cborMapConverter") AttributeConverter<Map<String, Serializable>, byte[]> cborConverter
    ) {
        this.converter = cborConverter;
    }

    public Run build(RunEntity entity) {
        //read metadata map as-is
        Map<String, Serializable> meta = converter.convertToEntityAttribute(entity.getMetadata());

        // build metadata
        RunMetadata metadata = new RunMetadata();
        metadata.configure(meta);

        if (!StringUtils.hasText(metadata.getVersion())) {
            metadata.setVersion(entity.getId());
        }
        if (!StringUtils.hasText(metadata.getName())) {
            metadata.setName(entity.getName());
        }
        metadata.setProject(entity.getProject());
        metadata.setCreated(
            entity.getCreated() != null
                ? OffsetDateTime.ofInstant(entity.getCreated().toInstant(), ZoneOffset.UTC)
                : null
        );
        metadata.setUpdated(
            entity.getUpdated() != null
                ? OffsetDateTime.ofInstant(entity.getUpdated().toInstant(), ZoneOffset.UTC)
                : null
        );

        return Run
            .builder()
            .id(entity.getId())
            .kind(entity.getKind())
            .project(entity.getProject())
            .user(entity.getCreatedBy())
            .metadata(MapUtils.mergeMultipleMaps(meta, metadata.toMap()))
            .spec(converter.convertToEntityAttribute(entity.getSpec()))
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
    public Run convert(RunEntity source) {
        return build(source);
    }
}
