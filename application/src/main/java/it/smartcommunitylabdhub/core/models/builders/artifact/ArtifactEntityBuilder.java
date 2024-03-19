package it.smartcommunitylabdhub.core.models.builders.artifact;

import it.smartcommunitylabdhub.commons.accessors.fields.StatusFieldAccessor;
import it.smartcommunitylabdhub.commons.models.entities.artifact.Artifact;
import it.smartcommunitylabdhub.commons.models.entities.artifact.ArtifactMetadata;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.core.models.converters.types.CBORConverter;
import it.smartcommunitylabdhub.core.models.entities.artifact.ArtifactEntity;
import java.time.ZoneOffset;
import java.util.Date;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ArtifactEntityBuilder implements Converter<Artifact, ArtifactEntity> {

    private final CBORConverter cborConverter;

    public ArtifactEntityBuilder(CBORConverter cborConverter) {
        this.cborConverter = cborConverter;
    }

    /**
     * Build an artifact from an artifactDTO and store extra values as a cbor
     *
     * @param dto the artifact DTO
     * @return Artifact
     */
    public ArtifactEntity build(Artifact dto) {
        // Retrieve Field accessor
        StatusFieldAccessor statusFieldAccessor = StatusFieldAccessor.with(dto.getStatus());
        ArtifactMetadata metadata = new ArtifactMetadata();
        metadata.configure(dto.getMetadata());

        return ArtifactEntity
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
    public ArtifactEntity convert(Artifact source) {
        return build(source);
    }
}
