package it.smartcommunitylabdhub.commons.infrastructure.factories.accessors;

import it.smartcommunitylabdhub.commons.models.accessors.kinds.interfaces.Accessor;

public interface AccessorFactory<T extends Accessor<Object>> {
  T create();
}
