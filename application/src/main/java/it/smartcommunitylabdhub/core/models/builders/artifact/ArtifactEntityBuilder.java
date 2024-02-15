package it.smartcommunitylabdhub.core.models.builders.artifact;

import it.smartcommunitylabdhub.commons.accessors.fields.StatusFieldAccessor;
import it.smartcommunitylabdhub.commons.models.entities.artifact.Artifact;
import it.smartcommunitylabdhub.commons.models.entities.artifact.ArtifactMetadata;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.services.SpecRegistry;
import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.types.CBORConverter;
import it.smartcommunitylabdhub.core.models.entities.artifact.ArtifactEntity;
import java.io.Serializable;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ArtifactEntityBuilder implements Converter<Artifact, ArtifactEntity> {

    @Autowired
    CBORConverter cborConverter;

    @Autowired
    SpecRegistry specRegistry;

    /**
     * Build an artifact from an artifactDTO and store extra values as a cbor
     *
     * @param dto the artifact DTO
     * @return Artifact
     */
    public ArtifactEntity build(Artifact dto) {
        // Parse and export Spec
        Map<String, Serializable> spec = specRegistry
            .createSpec(dto.getKind(), EntityName.ARTIFACT, dto.getSpec())
            .toMap();

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
            .spec(cborConverter.convert(spec))
            .status(cborConverter.convert(dto.getStatus()))
            .extra(cborConverter.convert(dto.getExtra()))
            .state(
                // Store status if not present
                statusFieldAccessor.getState() == null ? State.CREATED : State.valueOf(statusFieldAccessor.getState())
            )
            // Metadata Extraction
            .embedded(metadata.getEmbedded() == null ? Boolean.FALSE : metadata.getEmbedded())
            .created(metadata.getCreated())
            .updated(metadata.getUpdated())
            .build();
    }

    @Override
    public ArtifactEntity convert(Artifact source) {
        return build(source);
    }

    /**
     * Update an artifact if element is not passed it override causing empty field
     *
     * @param artifact    the Artifact entity
     * @param artifactDTO the ArtifactDTO to combine with the entity
     * @return Artifact
     */
    public ArtifactEntity update(ArtifactEntity artifact, Artifact artifactDTO) {
        ArtifactEntity newArtifact = build(artifactDTO);

        return doUpdate(artifact, newArtifact);
    }

    private ArtifactEntity doUpdate(ArtifactEntity artifact, ArtifactEntity newArtifact) {
        return EntityFactory.combine(
            artifact,
            newArtifact,
            builder ->
                builder
                    .withIfElse(
                        newArtifact.getState().name().equals(State.NONE.name()),
                        (a, condition) -> {
                            if (condition) {
                                a.setState(State.CREATED);
                            } else {
                                a.setState(newArtifact.getState());
                            }
                        }
                    )
                    .with(e -> e.setMetadata(newArtifact.getMetadata()))
                    .with(e -> e.setExtra(newArtifact.getExtra()))
                    .with(e -> e.setStatus(newArtifact.getStatus()))
                    .with(e -> e.setMetadata(newArtifact.getMetadata()))
                    // Metadata Extraction
                    .withIfElse(
                        newArtifact.getEmbedded() == null,
                        (e, condition) -> {
                            if (condition) {
                                e.setEmbedded(false);
                            } else {
                                e.setEmbedded(newArtifact.getEmbedded());
                            }
                        }
                    )
        );
    }
}
