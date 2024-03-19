package it.smartcommunitylabdhub.core.models.builders.secret;

import it.smartcommunitylabdhub.commons.accessors.fields.StatusFieldAccessor;
import it.smartcommunitylabdhub.commons.models.entities.secret.Secret;
import it.smartcommunitylabdhub.commons.models.entities.secret.SecretMetadata;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.core.models.converters.types.CBORConverter;
import it.smartcommunitylabdhub.core.models.entities.secret.SecretEntity;
import java.time.ZoneOffset;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class SecretEntityBuilder implements Converter<Secret, SecretEntity> {

    @Autowired
    CBORConverter cborConverter;

    /**
     * Build a secret from a secretDTO and store extra values as f cbor
     * <p>
     *
     * @param dto the secretDTO that need to be stored
     * @return Secret
     */
    public SecretEntity build(Secret dto) {
        // Retrieve field accessor
        StatusFieldAccessor statusFieldAccessor = StatusFieldAccessor.with(dto.getStatus());
        SecretMetadata metadata = new SecretMetadata();
        metadata.configure(dto.getMetadata());

        return SecretEntity
            .builder()
            .id(dto.getId())
            .name(dto.getName())
            .kind(dto.getKind())
            .project(dto.getProject())
            .metadata(cborConverter.convert(dto.getMetadata()))
            .spec(cborConverter.convert(dto.getSpec()))
            .status(cborConverter.convert(dto.getStatus()))
            .extra(cborConverter.convert(dto.getExtra()))
            .state(
                // Store status if not present
                statusFieldAccessor.getState() == null ? State.CREATED : State.valueOf(statusFieldAccessor.getState())
            )
            // Metadata Extraction
            .embedded(metadata.getEmbedded() == null ? Boolean.FALSE : metadata.getEmbedded())
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
    public SecretEntity convert(Secret source) {
        return build(source);
    }
}
