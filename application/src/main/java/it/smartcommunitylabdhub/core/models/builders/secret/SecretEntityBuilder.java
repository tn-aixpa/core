package it.smartcommunitylabdhub.core.models.builders.secret;

import it.smartcommunitylabdhub.commons.accessors.fields.StatusFieldAccessor;
import it.smartcommunitylabdhub.commons.models.entities.secret.Secret;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.models.metadata.BaseMetadata;
import it.smartcommunitylabdhub.commons.models.metadata.EmbeddableMetadata;
import it.smartcommunitylabdhub.core.models.entities.SecretEntity;
import jakarta.persistence.AttributeConverter;
import java.io.Serializable;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class SecretEntityBuilder implements Converter<Secret, SecretEntity> {

    private final AttributeConverter<Map<String, Serializable>, byte[]> converter;

    public SecretEntityBuilder(
        @Qualifier("cborMapConverter") AttributeConverter<Map<String, Serializable>, byte[]> converter
    ) {
        this.converter = converter;
    }

    /**
     * Build a secret from a secretDTO and store extra values as f cbor
     * <p>
     *
     * @param dto the secretDTO that need to be stored
     * @return Secret
     */
    public SecretEntity build(Secret dto) {
        // Extract data
        StatusFieldAccessor statusFieldAccessor = StatusFieldAccessor.with(dto.getStatus());
        BaseMetadata metadata = BaseMetadata.from(dto.getMetadata());
        EmbeddableMetadata embeddable = EmbeddableMetadata.from(dto.getMetadata());

        return SecretEntity
            .builder()
            .id(dto.getId())
            .name(dto.getName())
            .kind(dto.getKind())
            .project(dto.getProject())
            .metadata(converter.convertToDatabaseColumn(dto.getMetadata()))
            .spec(converter.convertToDatabaseColumn(dto.getSpec()))
            .status(converter.convertToDatabaseColumn(dto.getStatus()))
            .state(
                // Store status if not present
                statusFieldAccessor.getState() == null ? State.CREATED : State.valueOf(statusFieldAccessor.getState())
            )
            // Metadata Extraction
            .embedded(embeddable.getEmbedded() == null ? Boolean.FALSE : embeddable.getEmbedded())
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
    public SecretEntity convert(Secret source) {
        return build(source);
    }
}
