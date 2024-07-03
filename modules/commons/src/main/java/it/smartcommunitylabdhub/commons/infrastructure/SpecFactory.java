package it.smartcommunitylabdhub.commons.infrastructure;

import it.smartcommunitylabdhub.commons.models.specs.Spec;

@FunctionalInterface
public interface SpecFactory<T extends Spec> {
    T create();
}
