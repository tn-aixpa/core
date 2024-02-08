package it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces;

import java.util.Map;

public interface RunFieldAccessor<O extends RunFieldAccessor<O>> extends CommonFieldAccessor<O> {

    default String getTask() {
        return (String) ((Map<String, Object>) getField("spec")).get("task");
    }
}
