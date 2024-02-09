package it.smartcommunitylabdhub.commons.infrastructure.factories.specs;

import it.smartcommunitylabdhub.commons.models.base.interfaces.Spec;

public interface SpecFactory<T extends Spec> {
  T create();
}
