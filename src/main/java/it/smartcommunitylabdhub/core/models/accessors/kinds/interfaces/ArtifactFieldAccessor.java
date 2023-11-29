package it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces;

import it.smartcommunitylabdhub.core.models.enums.State;

public interface ArtifactFieldAccessor<O extends ArtifactFieldAccessor<O>> extends CommonFieldAccessor<O> {
    default String getState() {
        return (String) this.getStatus()
                .getOrDefault("state", State.NONE);
    }
}
