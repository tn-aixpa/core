package it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces;

import it.smartcommunitylabdhub.core.models.enums.State;

import java.util.Map;
import java.util.Optional;

/**
 * Define base accessor
 */
public interface CommonFieldAccessor<O extends CommonFieldAccessor<O>> extends Accessor<Object> {

    default String getName() {
        return (String) getField("name");
    }

    default String getProject() {
        return (String) getField("project");
    }

    default String getKind() {
        return (String) getField("kind");
    }

    @SuppressWarnings("unchecked")
    default Map<String, Object> getMetadata() {
        return (Map<String, Object>) getField("metadata");
    }

    @SuppressWarnings("unchecked")
    default Map<String, Object> getSpecs() {
        return (Map<String, Object>) getField("spec");
    }

    @SuppressWarnings("unchecked")
    default Map<String, Object> getStatus() {
        return (Map<String, Object>) getField("status");
    }

    default String getState() {
        return (String) Optional.ofNullable(this.getStatus())
                .map(status -> status.getOrDefault("state", State.NONE.name()))
                .orElseGet(State.NONE::name);

    }
}
