package it.smartcommunitylabdhub.core.models.builders.artifact;

import it.smartcommunitylabdhub.commons.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.commons.infrastructure.factories.specs.SpecRegistry;
import it.smartcommunitylabdhub.commons.models.accessors.kinds.interfaces.ArtifactFieldAccessor;
import it.smartcommunitylabdhub.commons.models.entities.artifact.Artifact;
import it.smartcommunitylabdhub.commons.models.entities.artifact.specs.ArtifactBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.utils.jackson.JacksonMapper;
import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.entities.artifact.ArtifactEntity;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ArtifactEntityBuilder {

  @Autowired
  SpecRegistry specRegistry;

  /**
   * Build an artifact from an artifactDTO and store extra values as a cbor
   *
   * @param artifactDTO the artifact DTO
   * @return Artifact
   */
  public ArtifactEntity build(Artifact artifactDTO) {
    // Validate Spec
    specRegistry.createSpec(
      artifactDTO.getKind(),
      EntityName.ARTIFACT,
      Map.of()
    );

    // Retrieve Field accessor
    ArtifactFieldAccessor artifactFieldAccessor = ArtifactFieldAccessor.with(
      JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
        artifactDTO,
        JacksonMapper.typeRef
      )
    );

    // Retrieve Spec
    ArtifactBaseSpec spec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
      artifactDTO.getSpec(),
      ArtifactBaseSpec.class
    );

    return EntityFactory.combine(
      ArtifactEntity.builder().build(),
      artifactDTO,
      builder ->
        builder
          // check id
          .withIf(
            artifactDTO.getId() != null,
            a -> a.setId(artifactDTO.getId())
          )
          .with(a -> a.setName(artifactDTO.getName()))
          .with(a -> a.setKind(artifactDTO.getKind()))
          .with(a -> a.setProject(artifactDTO.getProject()))
          .with(a ->
            a.setMetadata(
              ConversionUtils.convert(artifactDTO.getMetadata(), "metadata")
            )
          )
          .with(a ->
            a.setExtra(ConversionUtils.convert(artifactDTO.getExtra(), "cbor"))
          )
          .with(a ->
            a.setExtra(ConversionUtils.convert(artifactDTO.getStatus(), "cbor"))
          )
          .with(a -> a.setSpec(ConversionUtils.convert(spec.toMap(), "cbor")))
          // Store status if not present
          .withIfElse(
            artifactFieldAccessor.getState().equals(State.NONE.name()),
            (a, condition) -> {
              if (condition) {
                a.setState(State.CREATED);
              } else {
                a.setState(State.valueOf(artifactFieldAccessor.getState()));
              }
            }
          )
          // Metadata Extraction
          .withIfElse(
            artifactDTO.getMetadata().getEmbedded() == null,
            (a, condition) -> {
              if (condition) {
                a.setEmbedded(false);
              } else {
                a.setEmbedded(artifactDTO.getMetadata().getEmbedded());
              }
            }
          )
          .withIf(
            artifactDTO.getMetadata().getCreated() != null,
            a -> a.setCreated(artifactDTO.getMetadata().getCreated())
          )
          .withIf(
            artifactDTO.getMetadata().getUpdated() != null,
            a -> a.setUpdated(artifactDTO.getMetadata().getUpdated())
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
    ArtifactEntity newArtifact = build(artifactDTO);

    return doUpdate(artifact, newArtifact);
  }

  private ArtifactEntity doUpdate(
    ArtifactEntity artifact,
    ArtifactEntity newArtifact
  ) {
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
          .with(a -> a.setMetadata(newArtifact.getMetadata()))
          .with(a -> a.setExtra(newArtifact.getExtra()))
          .with(a -> a.setExtra(newArtifact.getStatus()))
          .with(a -> a.setMetadata(newArtifact.getMetadata()))
          // Metadata Extraction
          .withIfElse(
            newArtifact.getEmbedded() == null,
            (a, condition) -> {
              if (condition) {
                a.setEmbedded(false);
              } else {
                a.setEmbedded(newArtifact.getEmbedded());
              }
            }
          )
    );
  }
}
