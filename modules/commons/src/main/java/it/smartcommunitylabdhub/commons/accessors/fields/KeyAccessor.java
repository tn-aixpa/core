package it.smartcommunitylabdhub.commons.accessors.fields;

import io.micrometer.common.lang.Nullable;
import it.smartcommunitylabdhub.commons.accessors.Accessor;
import java.util.Map;

/**
 * Status field common accessor
 */
public interface KeyAccessor extends Accessor<String> {
    default @Nullable String getProject() {
        return get("project");
    }
    default @Nullable String getType() {
        return get("type");
    }

    default @Nullable String getKind() {
        return get("kind");
    }

    default @Nullable String getName() {
        return get("name");
    }

    default @Nullable String getId() {
        return get("id");
    }

    static KeyAccessor with(Map<String, String> map) {
        return () -> map;
    }
}
