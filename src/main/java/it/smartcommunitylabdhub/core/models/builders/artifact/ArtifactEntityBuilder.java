package it.smartcommunitylabdhub.core.models.builders.artifact;

import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.accessors.AccessorRegistry;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecRegistry;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.Accessor;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.ArtifactFieldAccessor;
import it.smartcommunitylabdhub.core.models.base.interfaces.Spec;
import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.entities.artifact.Artifact;
import it.smartcommunitylabdhub.core.models.entities.artifact.ArtifactEntity;
import it.smartcommunitylabdhub.core.models.entities.artifact.specs.ArtifactBaseSpec;
import it.smartcommunitylabdhub.core.models.enums.State;
import it.smartcommunitylabdhub.core.utils.jackson.JacksonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class ArtifactEntityBuilder {

    @Autowired
    SpecRegistry<? extends Spec> specRegistry;

    @Autowired
    AccessorRegistry<? extends Accessor<Object>> accessorRegistry;

    /**
     * Build an artifact from an artifactDTO and store extra values as a cbor
     *
     * @param artifactDTO the artifact DTO
     * @return Artifact
     */
    public ArtifactEntity build(Artifact artifactDTO) {

        // Validate Spec
        specRegistry.createSpec(artifactDTO.getKind(), EntityName.ARTIFACT, Map.of());

        // Retrieve Field accessor
        ArtifactFieldAccessor<?> artifactFieldAccessor =
                accessorRegistry.createAccessor(
                        artifactDTO.getKind(),
                        EntityName.ARTIFACT,
                        JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
                                artifactDTO,
                                JacksonMapper.typeRef)
                );

        // Retrieve Spec
        ArtifactBaseSpec<?> spec = JacksonMapper.CUSTOM_OBJECT_MAPPER
                .convertValue(artifactDTO.getSpec(), ArtifactBaseSpec.class);


        return EntityFactory.combine(
                ConversionUtils.convert(artifactDTO, "artifact"), artifactDTO,
                builder -> builder

                        // check id
                        .withIf(artifactDTO.getId() != null,
                                (a) -> a.setId(artifactDTO.getId()))
                        .with(a -> a.setMetadata(ConversionUtils.convert(
                                artifactDTO.getMetadata(), "metadata")))
                        .with(a -> a.setExtra(ConversionUtils.convert(
                                artifactDTO.getExtra(), "cbor")))
                        .with(a -> a.setExtra(ConversionUtils.convert(
                                artifactDTO.getStatus(), "cbor")))
                        .with(a -> a.setSpec(ConversionUtils.convert(
                                spec.toMap(), "cbor")))

                        // Store status if not present
                        .withIfElse(artifactFieldAccessor.getState().equals(State.NONE.name()),
                                (a, condition) -> {
                                    if (condition) {
                                        a.setState(State.CREATED);
                                    } else {
                                        a.setState(State.valueOf(artifactFieldAccessor.getState()));
                                    }
                                }
                        )

                        // Metadata Extraction
                        .withIfElse(artifactDTO.getMetadata().getEmbedded() == null,
                                (a, condition) -> {
                                    if (condition) {
                                        a.setEmbedded(false);
                                    } else {
                                        a.setEmbedded(artifactDTO.getMetadata().getEmbedded());
                                    }
                                }
                        )
                        .withIf(artifactDTO.getMetadata().getCreated() != null, (a) ->
                                a.setCreated(artifactDTO.getMetadata().getCreated()))
                        .withIf(artifactDTO.getMetadata().getUpdated() != null, (a) ->
                                a.setUpdated(artifactDTO.getMetadata().getUpdated())
                        )


        );

    }

    /**
     * Update an artifact if element is not passed it override causing empty field
     *
     * @param artifact    the Artifact entity
     * @param artifactDTO the ArtifactDTO to combine with the entity
     * @return Artifact
     */
    public ArtifactEntity update(ArtifactEntity artifact, Artifact artifactDTO) {

        // Validate Spec
        specRegistry.createSpec(artifactDTO.getKind(), EntityName.ARTIFACT, Map.of());

        // Retrieve Field accessor
        ArtifactFieldAccessor<?> artifactFieldAccessor =
                accessorRegistry.createAccessor(
                        artifactDTO.getKind(),
                        EntityName.ARTIFACT,
                        JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
                                artifactDTO,
                                JacksonMapper.typeRef)
                );

        return EntityFactory.combine(
                artifact, artifactDTO, builder -> builder
                        .withIfElse(artifactFieldAccessor.getState().equals(State.NONE.name()),
                                (a, condition) -> {
                                    if (condition) {
                                        a.setState(State.CREATED);
                                    } else {
                                        a.setState(State.valueOf(artifactFieldAccessor.getState()));
                                    }
                                }
                        )
                        .with(a -> a.setMetadata(ConversionUtils.convert(
                                artifactDTO.getMetadata(), "metadata")))
                        .with(a -> a.setExtra(ConversionUtils.convert(
                                artifactDTO.getExtra(), "cbor")))
                        .with(a -> a.setExtra(ConversionUtils.convert(
                                artifactDTO.getStatus(), "cbor")))
                        // Metadata Extraction
                        .withIfElse(artifactDTO.getMetadata().getEmbedded() == null,
                                (a, condition) -> {
                                    if (condition) {
                                        a.setEmbedded(false);
                                    } else {
                                        a.setEmbedded(artifactDTO.getMetadata().getEmbedded());
                                    }
                                }
                        )
        );
    }
}
