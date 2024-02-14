package it.smartcommunitylabdhub.commons.infrastructure;

import it.smartcommunitylabdhub.commons.models.specs.Spec;

public interface SpecFactory<T extends Spec> {
    T create();
}
