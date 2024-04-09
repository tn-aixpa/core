package it.smartcommunitylabdhub.core.models.builders.artifact;

import it.smartcommunitylabdhub.commons.models.entities.artifact.Artifact;
import it.smartcommunitylabdhub.commons.models.entities.artifact.ArtifactMetadata;
import it.smartcommunitylabdhub.commons.utils.MapUtils;
import it.smartcommunitylabdhub.core.models.entities.ArtifactEntity;
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
public class ArtifactDTOBuilder implements Converter<ArtifactEntity, Artifact> {

    private final AttributeConverter<Map<String, Serializable>, byte[]> converter;

    public ArtifactDTOBuilder(
        @Qualifier("cborMapConverter") AttributeConverter<Map<String, Serializable>, byte[]> cborConverter
    ) {
        this.converter = cborConverter;
    }

    public Artifact build(ArtifactEntity entity) {
        //read metadata map as-is
        Map<String, Serializable> meta = converter.convertToEntityAttribute(entity.getMetadata());

        // build metadata
        ArtifactMetadata metadata = new ArtifactMetadata();
        metadata.configure(meta);

        if (!StringUtils.hasText(metadata.getVersion())) {
            metadata.setVersion(entity.getId());
        }
        if (!StringUtils.hasText(metadata.getName())) {
            metadata.setName(entity.getName());
        }
        metadata.setProject(entity.getProject());
        metadata.setEmbedded(entity.getEmbedded());
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

        return Artifact
            .builder()
            .id(entity.getId())
            .name(entity.getName())
            .kind(entity.getKind())
            .project(entity.getProject())
            .user(entity.getCreatedBy())
            .metadata(MapUtils.mergeMultipleMaps(meta, metadata.toMap()))
            .spec(converter.convertToEntityAttribute(entity.getSpec()))
            .status(
                MapUtils.mergeMultipleMaps(
                    converter.convertToEntityAttribute(entity.getStatus()),
                    Map.of("state", entity.getState().toString())
                )
            )
            .build();
    }

    @Override
    public Artifact convert(ArtifactEntity source) {
        return build(source);
    }
}
