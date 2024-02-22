package it.smartcommunitylabdhub.core.models.builders.secret;

import it.smartcommunitylabdhub.commons.accessors.fields.StatusFieldAccessor;
import it.smartcommunitylabdhub.commons.models.entities.secret.Secret;
import it.smartcommunitylabdhub.commons.models.entities.secret.SecretMetadata;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.services.SpecRegistry;
import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.types.CBORConverter;
import it.smartcommunitylabdhub.core.models.entities.secret.SecretEntity;
import java.io.Serializable;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class SecretEntityBuilder implements Converter<Secret, SecretEntity> {

    @Autowired
    CBORConverter cborConverter;

    @Autowired
    SpecRegistry specRegistry;

    /**
     * Build a secret from a secretDTO and store extra values as f cbor
     * <p>
     *
     * @param dto the secretDTO that need to be stored
     * @return Secret
     */
    public SecretEntity build(Secret dto) {
        // Parse and export Spec
        Map<String, Serializable> spec = specRegistry
            .createSpec(dto.getKind(), EntityName.SECRET, dto.getSpec())
            .toMap();

        // Retrieve field accessor
        StatusFieldAccessor statusFieldAccessor = StatusFieldAccessor.with(dto.getStatus());
        SecretMetadata metadata = new SecretMetadata();
        metadata.configure(dto.getMetadata());

        return EntityFactory.combine(
            SecretEntity.builder().build(),
            builder ->
                builder
                    // check id
                    .withIf(dto.getId() != null, e -> e.setId(dto.getId()))
                    .with(e -> e.setKind(dto.getKind()))
                    .with(e -> e.setName(dto.getName()))
                    .with(e -> e.setProject(dto.getProject()))
                    .with(e -> e.setMetadata(cborConverter.convert(dto.getMetadata())))
                    .with(e -> e.setSpec(cborConverter.convert(spec)))
                    .with(e -> e.setStatus(cborConverter.convert(dto.getStatus())))
                    .with(e -> e.setExtra(cborConverter.convert(dto.getExtra())))
                    // Store status if not present
                    .withIfElse(
                        (statusFieldAccessor.getState() == null),
                        (e, condition) -> {
                            if (condition) {
                                e.setState(State.CREATED);
                            } else {
                                e.setState(State.valueOf(statusFieldAccessor.getState()));
                            }
                        }
                    )
                    // Metadata Extraction
                    .withIfElse(
                        metadata.getEmbedded() == null,
                        (e, condition) -> {
                            if (condition) {
                                e.setEmbedded(false);
                            } else {
                                e.setEmbedded(metadata.getEmbedded());
                            }
                        }
                    )
                    .withIf(metadata.getCreated() != null, e -> e.setCreated(metadata.getCreated()))
                    .withIf(metadata.getUpdated() != null, e -> e.setUpdated(metadata.getUpdated()))
        );
    }

    @Override
    public SecretEntity convert(Secret source) {
        return build(source);
    }

    /**
     * Update a secret if element is not passed it override causing empty field
     *
     * @param secret the secret to update
     * @return Secret
     */
    public SecretEntity update(SecretEntity secret, Secret secretDTO) {
        SecretEntity newSecret = build(secretDTO);
        return doUpdate(secret, newSecret);
    }

    /**
     * Updates the secret entity with the new secret entity and returns the combined entity.
     *
     * @param secret    the original secret entity
     * @param newSecret the new secret entity to update with
     * @return the combined entity after the update
     */
    private SecretEntity doUpdate(SecretEntity secret, SecretEntity newSecret) {
        return EntityFactory.combine(
            secret,
            builder ->
                builder
                    .withIfElse(
                        newSecret.getState().name().equals(State.NONE.name()),
                        (f, condition) -> {
                            if (condition) {
                                f.setState(State.CREATED);
                            } else {
                                f.setState(newSecret.getState());
                            }
                        }
                    )
                    .with(e -> e.setMetadata(newSecret.getMetadata()))
                    .with(e -> e.setExtra(newSecret.getExtra()))
                    .with(e -> e.setStatus(newSecret.getStatus()))
                    // Metadata Extraction
                    .withIfElse(
                        newSecret.getEmbedded() == null,
                        (e, condition) -> {
                            if (condition) {
                                e.setEmbedded(false);
                            } else {
                                e.setEmbedded(newSecret.getEmbedded());
                            }
                        }
                    )
        );
    }
}
