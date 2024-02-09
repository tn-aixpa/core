package it.smartcommunitylabdhub.core.models.builders.artifact;

import it.smartcommunitylabdhub.commons.models.entities.artifact.Artifact;
import it.smartcommunitylabdhub.commons.models.entities.artifact.metadata.ArtifactMetadata;
import it.smartcommunitylabdhub.commons.utils.MapUtils;
import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.converters.types.MetadataConverter;
import it.smartcommunitylabdhub.core.models.entities.artifact.ArtifactEntity;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ArtifactDTOBuilder {

  @Autowired
  MetadataConverter<ArtifactMetadata> metadataConverter;

  public Artifact build(ArtifactEntity artifact, Boolean embeddable) {
    return EntityFactory.create(
      Artifact::new,
      artifact,
      builder ->
        builder
          .with(dto -> dto.setId(artifact.getId()))
          .with(dto -> dto.setKind(artifact.getKind()))
          .with(dto -> dto.setProject(artifact.getProject()))
          .with(dto -> dto.setName(artifact.getName()))
          .with(dto -> {
            // Set Metadata for artifact
            ArtifactMetadata artifactMetadata = Optional
              .ofNullable(
                metadataConverter.reverseByClass(
                  artifact.getMetadata(),
                  ArtifactMetadata.class
                )
              )
              .orElseGet(ArtifactMetadata::new);

            if (!StringUtils.hasText(artifactMetadata.getVersion())) {
              artifactMetadata.setVersion(artifact.getId());
            }
            if (!StringUtils.hasText(artifactMetadata.getName())) {
              artifactMetadata.setName(artifact.getName());
            }

            artifactMetadata.setProject(artifact.getProject());
            artifactMetadata.setEmbedded(artifact.getEmbedded());
            artifactMetadata.setCreated(artifact.getCreated());
            artifactMetadata.setUpdated(artifact.getUpdated());
            dto.setMetadata(artifactMetadata);
          })
          .withIfElse(
            embeddable,
            (dto, condition) ->
              Optional
                .ofNullable(artifact.getEmbedded())
                .filter(embedded -> !condition || embedded)
                .ifPresent(embedded ->
                  dto.setSpec(
                    ConversionUtils.reverse(artifact.getSpec(), "cbor")
                  )
                )
          )
          .withIfElse(
            embeddable,
            (dto, condition) ->
              Optional
                .ofNullable(artifact.getEmbedded())
                .filter(embedded -> !condition || embedded)
                .ifPresent(embedded ->
                  dto.setExtra(
                    ConversionUtils.reverse(artifact.getExtra(), "cbor")
                  )
                )
          )
          .withIfElse(
            embeddable,
            (dto, condition) ->
              Optional
                .ofNullable(artifact.getEmbedded())
                .filter(embedded -> !condition || embedded)
                .ifPresent(embedded ->
                  dto.setStatus(
                    MapUtils.mergeMultipleMaps(
                      ConversionUtils.reverse(artifact.getStatus(), "cbor"),
                      Map.of("state", artifact.getState())
                    )
                  )
                )
          )
    );
  }
}
