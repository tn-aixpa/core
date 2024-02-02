package it.smartcommunitylabdhub.core.models.builders.secret;

import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.converters.types.MetadataConverter;
import it.smartcommunitylabdhub.core.models.entities.secret.Secret;
import it.smartcommunitylabdhub.core.models.entities.secret.SecretEntity;
import it.smartcommunitylabdhub.core.models.entities.secret.metadata.SecretMetadata;
import it.smartcommunitylabdhub.core.utils.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Optional;

@Component
public class SecretDTOBuilder {

    @Autowired
    MetadataConverter<SecretMetadata> metadataConverter;

    public Secret build(
            SecretEntity secret,
            boolean embeddable) {

        return EntityFactory.create(Secret::new, secret, builder -> builder
                .with(dto -> dto.setId(secret.getId()))
                .with(dto -> dto.setKind(secret.getKind()))
                .with(dto -> dto.setProject(secret.getProject()))
                .with(dto -> dto.setName(secret.getName()))
                .with(dto -> {
                    // Set Metadata for secret
                    SecretMetadata secretMetadata =
                            Optional.ofNullable(metadataConverter.reverseByClass(
                                    secret.getMetadata(),
                                    SecretMetadata.class)
                            ).orElseGet(SecretMetadata::new);

                    if (!StringUtils.hasText(secretMetadata.getVersion())) {
                        secretMetadata.setVersion(secret.getId());
                    }
                    if (!StringUtils.hasText(secretMetadata.getName())) {
                        secretMetadata.setName(secret.getName());
                    }

                    secretMetadata.setProject(secret.getProject());
                    secretMetadata.setEmbedded(secret.getEmbedded());
                    secretMetadata.setCreated(secret.getCreated());
                    secretMetadata.setUpdated(secret.getUpdated());
                    dto.setMetadata(secretMetadata);
                })
                .withIfElse(embeddable, (dto, condition) -> Optional
                        .ofNullable(secret.getEmbedded())
                        .filter(embedded -> !condition || embedded)
                        .ifPresent(embedded -> dto
                                .setSpec(ConversionUtils.reverse(
                                        secret.getSpec(),
                                        "cbor"))))
                .withIfElse(embeddable, (dto, condition) -> Optional
                        .ofNullable(secret.getEmbedded())
                        .filter(embedded -> !condition || embedded)
                        .ifPresent(embedded -> dto.setExtra(
                                ConversionUtils.reverse(
                                        secret.getExtra(),
                                        "cbor"))))
                .withIfElse(embeddable, (dto, condition) -> Optional
                        .ofNullable(secret.getEmbedded())
                        .filter(embedded -> !condition || embedded)
                        .ifPresent(embedded -> dto.setStatus(
                                MapUtils.mergeMultipleMaps(
                                        ConversionUtils.reverse(
                                                secret.getStatus(),
                                                "cbor"),
                                        Map.of("state",
                                                secret.getState())
                                ))
                        )
                )

        );
    }
}
