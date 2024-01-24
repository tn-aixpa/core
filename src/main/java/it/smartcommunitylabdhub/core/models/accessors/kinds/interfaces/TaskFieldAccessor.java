package it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces;

import java.util.List;
import java.util.Map;

public interface TaskFieldAccessor<O extends TaskFieldAccessor<O>> extends CommonFieldAccessor<O> {

    @SuppressWarnings("unchecked")
    default List<Map<String, Object>> getVolumes() {
        return mapHasField(getSpecs(), "volumes") ? (List<Map<String, Object>>) getSpecs().get("volumes") : null;
    }

    @SuppressWarnings("unchecked")
    default Map<String, Object> getNodeSelector() {
        return mapHasField(getSpecs(), "node_selector")
                ? (Map<String, Object>) getSpecs().get("node_selector")
                : null;
    }

    @SuppressWarnings("unchecked")
    default List<Map<String, String>> getEnv() {
        return mapHasField(getSpecs(), "env")
                ? (List<Map<String, String>>) getSpecs().get("env")
                : null;
    }

    @SuppressWarnings("unchecked")
    default Map<String, Object> getResources() {
        return mapHasField(getSpecs(), "resources")
                ? (Map<String, Object>) getSpecs().get("resources")
                : null;
    }
}
