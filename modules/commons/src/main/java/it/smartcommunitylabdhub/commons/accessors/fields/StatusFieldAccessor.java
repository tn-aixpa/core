package it.smartcommunitylabdhub.commons.accessors.fields;

import it.smartcommunitylabdhub.commons.accessors.Accessor;
import java.util.Map;

/**
 * Status field common accessor
 */
public interface StatusFieldAccessor extends Accessor<Object> {
    default String getState() {
        return get("state");
    }

    static StatusFieldAccessor with(Map<String, Object> map) {
        return () -> map;
    }
}
