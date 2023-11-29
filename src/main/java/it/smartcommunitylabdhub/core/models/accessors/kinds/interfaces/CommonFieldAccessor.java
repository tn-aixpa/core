package it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces;

import java.util.Map;

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

}
