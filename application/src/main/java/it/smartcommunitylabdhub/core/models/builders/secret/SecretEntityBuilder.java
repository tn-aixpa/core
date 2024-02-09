package it.smartcommunitylabdhub.core.models.builders.secret;

import it.smartcommunitylabdhub.commons.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.commons.infrastructure.factories.specs.SpecRegistry;
import it.smartcommunitylabdhub.commons.models.accessors.kinds.interfaces.SecretFieldAccessor;
import it.smartcommunitylabdhub.commons.models.entities.secret.Secret;
import it.smartcommunitylabdhub.commons.models.entities.secret.specs.SecretBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.utils.jackson.JacksonMapper;
import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.entities.secret.SecretEntity;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SecretEntityBuilder {

  @Autowired
  SpecRegistry specRegistry;

  /**
   * Build a secret from a secretDTO and store extra values as f cbor
   * <p>
   *
   * @param secretDTO the secretDTO that need to be stored
   * @return Secret
   */
  public SecretEntity build(Secret secretDTO) {
    // Validate spec
    specRegistry.createSpec(secretDTO.getKind(), EntityName.SECRET, Map.of());

    // Retrieve field accessor
    SecretFieldAccessor secretFieldAccessor = SecretFieldAccessor.with(
      JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
        secretDTO,
        JacksonMapper.typeRef
      )
    );

    // Retrieve Spec
    SecretBaseSpec spec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
      secretDTO.getSpec(),
      SecretBaseSpec.class
    );

    return EntityFactory.combine(
      SecretEntity.builder().build(),
      secretDTO,
      builder ->
        builder
          // check id
          .withIf(secretDTO.getId() != null, f -> f.setId(secretDTO.getId()))
          .with(f -> f.setKind(secretDTO.getKind()))
          .with(f -> f.setName(secretDTO.getName()))
          .with(f -> f.setProject(secretDTO.getProject()))
          .with(f ->
            f.setMetadata(
              ConversionUtils.convert(secretDTO.getMetadata(), "metadata")
            )
          )
          .with(f ->
            f.setExtra(ConversionUtils.convert(secretDTO.getExtra(), "cbor"))
          )
          .with(f -> f.setSpec(ConversionUtils.convert(spec.toMap(), "cbor")))
          .with(f ->
            f.setStatus(ConversionUtils.convert(secretDTO.getStatus(), "cbor"))
          )
          // Store status if not present
          .withIfElse(
            secretFieldAccessor.getState().equals(State.NONE.name()),
            (f, condition) -> {
              if (condition) {
                f.setState(State.CREATED);
              } else {
                f.setState(State.valueOf(secretFieldAccessor.getState()));
              }
            }
          )
          // Metadata Extraction
          .withIfElse(
            secretDTO.getMetadata().getEmbedded() == null,
            (f, condition) -> {
              if (condition) {
                f.setEmbedded(false);
              } else {
                f.setEmbedded(secretDTO.getMetadata().getEmbedded());
              }
            }
          )
          .withIf(
            secretDTO.getMetadata().getCreated() != null,
            f -> f.setCreated(secretDTO.getMetadata().getCreated())
          )
          .withIf(
            secretDTO.getMetadata().getUpdated() != null,
            f -> f.setUpdated(secretDTO.getMetadata().getUpdated())
          )
    );
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
      newSecret,
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
          .with(f -> f.setMetadata(newSecret.getMetadata()))
          .with(f -> f.setExtra(newSecret.getExtra()))
          .with(f -> f.setStatus(newSecret.getStatus()))
          // Metadata Extraction
          .withIfElse(
            newSecret.getEmbedded() == null,
            (f, condition) -> {
              if (condition) {
                f.setEmbedded(false);
              } else {
                f.setEmbedded(newSecret.getEmbedded());
              }
            }
          )
    );
  }
}
