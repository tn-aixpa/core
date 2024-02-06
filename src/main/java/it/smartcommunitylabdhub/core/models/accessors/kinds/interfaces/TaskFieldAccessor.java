package it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TaskFieldAccessor<O extends TaskFieldAccessor<O>> extends CommonFieldAccessor<O> {

    @SuppressWarnings("unchecked")
    default List<Map<String, Object>> getVolumes() {
        return mapHasField(getSpecs(), "volumes") ? (List<Map<String, Object>>) getSpecs().get("volumes") : null;
    }

    @SuppressWarnings("unchecked")
    default List<Map<String, Object>> getNodeSelector() {
        return mapHasField(getSpecs(), "node_selector")
                ? (List<Map<String, Object>>) getSpecs().get("node_selector")
                : null;
    }

    @SuppressWarnings("unchecked")
    default List<Map<String, String>> getEnv() {
        return mapHasField(getSpecs(), "env")
                ? (List<Map<String, String>>) getSpecs().get("env")
                : null;
    }

    @SuppressWarnings("unchecked")
    default List<Map<String, Object>> getResources() {
        return mapHasField(getSpecs(), "resources")
                ? (List<Map<String, Object>>) getSpecs().get("resources")
                : null;
    }

    @SuppressWarnings("unchecked")
    default Set<String> getSecrets() {
        return mapHasField(getSpecs(), "secrets")
                ? (Set<String>) getSpecs().get("secrets")
                : null;
    }
}
