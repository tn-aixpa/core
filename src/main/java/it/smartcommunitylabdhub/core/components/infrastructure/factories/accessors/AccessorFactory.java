package it.smartcommunitylabdhub.core.components.infrastructure.factories.accessors;

import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.Accessor;

public interface AccessorFactory<T extends Accessor<Object>> {
    T create();
}
