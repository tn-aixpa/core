package it.smartcommunitylabdhub.commons.accessors.spec;

import it.smartcommunitylabdhub.commons.accessors.Accessor;
import java.util.Map;

public interface RunSpecAccessor extends Accessor<String> {
    default String getRuntime() {
        return get("runtime");
    }

    default String getTask() {
        return get("task");
    }

    default String getProject() {
        return get("project");
    }

    default String getFunction() {
        return get("function");
    }

    default String getVersion() {
        return get("version");
    }

    default boolean isValid() {
        return getProject() != null && getRuntime() != null && getFunction() != null && getVersion() != null;
    }

    static RunSpecAccessor with(Map<String, String> map) {
        return () -> map;
    }
}
