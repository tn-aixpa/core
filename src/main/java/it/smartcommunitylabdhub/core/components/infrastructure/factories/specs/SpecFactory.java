package it.smartcommunitylabdhub.core.components.infrastructure.factories.specs;

import it.smartcommunitylabdhub.core.models.base.interfaces.Spec;

public interface SpecFactory<T extends Spec> {
    T create();
}
