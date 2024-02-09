package it.smartcommunitylabdhub.core.models.validators.interfaces;

import it.smartcommunitylabdhub.commons.models.base.interfaces.Spec;
import it.smartcommunitylabdhub.commons.models.base.metadata.BaseMetadata;

public interface BaseValidator {
  <T extends Spec> boolean validateSpec(T spec);

  <T extends BaseMetadata> boolean validateMetadata(T metadata);
}
