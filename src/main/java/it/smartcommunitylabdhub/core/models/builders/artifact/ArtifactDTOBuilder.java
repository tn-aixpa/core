package it.smartcommunitylabdhub.core.models.builders.artifact;

import it.smartcommunitylabdhub.core.components.fsm.enums.ArtifactState;
import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.converters.types.MetadataConverter;
import it.smartcommunitylabdhub.core.models.entities.artifact.Artifact;
import it.smartcommunitylabdhub.core.models.entities.artifact.ArtifactEntity;
import it.smartcommunitylabdhub.core.models.entities.artifact.metadata.ArtifactMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ArtifactDTOBuilder {

    @Autowired
    MetadataConverter<ArtifactMetadata> metadataConverter;

    public Artifact build(ArtifactEntity artifact, Boolean embeddable) {


        return EntityFactory.create(Artifact::new, artifact, builder -> builder
                .with(dto -> dto.setId(artifact.getId()))
                .with(dto -> dto.setKind(artifact.getKind()))
                .with(dto -> dto.setProject(artifact.getProject()))
                .with(dto -> dto.setName(artifact.getName()))
                .withIfElse(embeddable, (dto, condition) -> Optional
                        .ofNullable(artifact.getEmbedded())
                        .filter(embedded -> !condition
                                || (condition && embedded))
                        .ifPresent(embedded -> dto.setMetadata(Optional
                                .ofNullable(metadataConverter
                                        .reverseByClass(artifact
                                                        .getMetadata(),
                                                ArtifactMetadata.class))
                                .orElseGet(ArtifactMetadata::new))))
                .withIfElse(embeddable, (dto, condition) -> Optional
                        .ofNullable(artifact.getEmbedded())
                        .filter(embedded -> !condition
                                || (condition && embedded))
                        .ifPresent(embedded -> dto
                                .setSpec(
                                        ConversionUtils.reverse(
                                                artifact.getSpec(),
                                                "cbor"))))
                .withIfElse(embeddable, (dto, condition) -> Optional
                        .ofNullable(artifact.getEmbedded())
                        .filter(embedded -> !condition
                                || (condition && embedded))
                        .ifPresent(embedded -> dto
                                .setExtra(ConversionUtils.reverse(
                                        artifact.getExtra(),
                                        "cbor"))))
                .withIfElse(embeddable, (dto, condition) -> Optional
                        .ofNullable(artifact.getEmbedded())
                        .filter(embedded -> !condition
                                || (condition && embedded))
                        .ifPresent(embedded -> dto
                                .setCreated(artifact.getCreated())))
                .withIfElse(embeddable, (dto, condition) -> Optional
                        .ofNullable(artifact.getEmbedded())
                        .filter(embedded -> !condition
                                || (condition && embedded))
                        .ifPresent(embedded -> dto
                                .setUpdated(artifact.getUpdated())))
                .withIfElse(embeddable, (dto, condition) -> Optional
                        .ofNullable(artifact.getEmbedded())
                        .filter(embedded -> !condition
                                || (condition && embedded))
                        .ifPresent(embedded -> dto
                                .setEmbedded(artifact
                                        .getEmbedded())))
                .withIfElse(embeddable, (dto, condition) ->

                        Optional.ofNullable(artifact.getEmbedded())
                                .filter(embedded -> !condition
                                        || (condition && embedded))
                                .ifPresent(embedded -> dto
                                        .setState(artifact
                                                .getState() == null
                                                ? ArtifactState.CREATED
                                                .name()
                                                : artifact.getState()
                                                .name()))

                )

        );
    }
}
