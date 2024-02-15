package it.smartcommunitylabdhub.commons.infrastructure;

import it.smartcommunitylabdhub.commons.models.specs.Spec;
import java.io.Serializable;
import java.util.Map;

public interface SpecFactory<T extends Spec> {
    T create();

    T create(Map<String, Serializable> data);
}
