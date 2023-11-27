package it.smartcommunitylabdhub.core.models.builders.artifact;

import it.smartcommunitylabdhub.core.components.fsm.enums.ArtifactState;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecEntity;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecRegistry;
import it.smartcommunitylabdhub.core.models.base.interfaces.Spec;
import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.entities.artifact.Artifact;
import it.smartcommunitylabdhub.core.models.entities.artifact.ArtifactEntity;
import it.smartcommunitylabdhub.core.models.entities.artifact.specs.ArtifactBaseSpec;
import it.smartcommunitylabdhub.core.utils.JacksonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ArtifactEntityBuilder {

    @Autowired
    SpecRegistry<? extends Spec> specRegistry;

    /**
     * Build a artifact from a artifactDTO and store extra values as a cbor
     *
     * @param artifactDTO the artifact DTO
     * @return Artifact
     */
    public ArtifactEntity build(Artifact artifactDTO) {

        specRegistry.createSpec(artifactDTO.getKind(), SpecEntity.ARTIFACT, Map.of());

        // Retrieve Spec
        ArtifactBaseSpec spec = JacksonMapper.objectMapper
                .convertValue(artifactDTO.getSpec(), ArtifactBaseSpec.class);

        return EntityFactory.combine(
                ConversionUtils.convert(artifactDTO, "artifact"), artifactDTO,
                builder -> builder
                        .with(p -> p.setMetadata(
                                ConversionUtils.convert(artifactDTO
                                                .getMetadata(),
                                        "metadata")))
                        .with(a -> a.setExtra(
                                ConversionUtils.convert(artifactDTO
                                                .getExtra(),

                                        "cbor")))
                        .with(a -> a.setSpec(ConversionUtils.convert(spec.toMap(),
                                "cbor"))));

    }

    /**
     * Update a artifact if element is not passed it override causing empty field
     *
     * @param artifact    the Artifact entity
     * @param artifactDTO the ArtifactDTO to combine with the entity
     * @return Artifact
     */
    public ArtifactEntity update(ArtifactEntity artifact, Artifact artifactDTO) {
        // Retrieve Spec
        ArtifactBaseSpec spec = JacksonMapper.objectMapper
                .convertValue(artifactDTO.getSpec(), ArtifactBaseSpec.class);

        return EntityFactory.combine(
                artifact, artifactDTO, builder -> builder
                        .with(a -> a.setState(artifactDTO.getState() == null
                                ? ArtifactState.CREATED
                                : ArtifactState.valueOf(
                                artifactDTO.getState())))
                        .with(a -> a.setMetadata(
                                ConversionUtils.convert(artifactDTO
                                                .getMetadata(),

                                        "metadata")))
                        .with(a -> a.setExtra(
                                ConversionUtils.convert(artifactDTO
                                                .getExtra(),

                                        "cbor")))
                        .with(a -> a.setSpec(
                                ConversionUtils.convert(spec.toMap(), "cbor")))
                        .with(a -> a.setEmbedded(
                                artifactDTO.getEmbedded())));
    }
}
