package it.smartcommunitylabdhub.commons.models.accessors.fields;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TaskFieldAccessor extends CommonFieldAccessor {
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
        return mapHasField(getSpecs(), "env") ? (List<Map<String, String>>) getSpecs().get("env") : null;
    }

    @SuppressWarnings("unchecked")
    default List<Map<String, Object>> getResources() {
        return mapHasField(getSpecs(), "resources") ? (List<Map<String, Object>>) getSpecs().get("resources") : null;
    }

    @SuppressWarnings("unchecked")
    default Set<String> getSecrets() {
        return mapHasField(getSpecs(), "secrets") ? (Set<String>) getSpecs().get("secrets") : null;
    }

    static TaskFieldAccessor with(Map<String, Object> map) {
        return () -> map;
    }
}
