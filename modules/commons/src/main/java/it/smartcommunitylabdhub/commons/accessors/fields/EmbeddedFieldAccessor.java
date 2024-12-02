package it.smartcommunitylabdhub.commons.accessors.fields;

import io.micrometer.common.lang.Nullable;
import it.smartcommunitylabdhub.commons.accessors.Accessor;
import java.io.Serializable;
import java.util.Map;

/**
 * Status field common accessor
 */
public interface EmbeddedFieldAccessor extends Accessor<Serializable> {
    default @Nullable Boolean getEmbedded() {
        return get("embedded");
    }

    default boolean isEmbedded() {
        return getEmbedded() != null ? getEmbedded().booleanValue() : false;
    }

    default @Nullable String getRef() {
        return get("ref");
    }

    static EmbeddedFieldAccessor with(Map<String, Serializable> map) {
        return () -> map;
    }
}
