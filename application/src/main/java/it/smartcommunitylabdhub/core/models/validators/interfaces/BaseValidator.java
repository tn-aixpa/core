package it.smartcommunitylabdhub.core.models.validators.interfaces;

import it.smartcommunitylabdhub.commons.models.base.BaseMetadata;
import it.smartcommunitylabdhub.commons.models.specs.Spec;

public interface BaseValidator {
    <T extends Spec> boolean validateSpec(T spec);

    <T extends BaseMetadata> boolean validateMetadata(T metadata);
}
