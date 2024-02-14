package it.smartcommunitylabdhub.commons.accessors.spec;

import it.smartcommunitylabdhub.commons.accessors.Accessor;
import java.util.Map;

public interface TaskSpecAccessor extends Accessor<String> {
    default String getRuntime() {
        return get("runtime");
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

    static TaskSpecAccessor with(Map<String, String> map) {
        return () -> map;
    }
}
