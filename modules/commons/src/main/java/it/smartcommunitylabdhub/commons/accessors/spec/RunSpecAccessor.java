package it.smartcommunitylabdhub.commons.accessors.spec;

import it.smartcommunitylabdhub.commons.accessors.Accessor;
import java.io.Serializable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface RunSpecAccessor extends Accessor<Serializable> {
    Pattern RUN_PATTERN = Pattern.compile("([^:/]+)://([^/]+)/([^/]+):(.+)");

    static RunSpecAccessor with(Map<String, Serializable> map) {
        return () -> map;
    }

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

    // Getter methods for individual parts of the task with prefix "task"
    default String getTaskKind() {
        String task = getTask();
        Matcher matcher = RUN_PATTERN.matcher(task);
        return matcher.matches() ? matcher.group(1) : "";
    }

    default String getTaskProject() {
        String task = getTask();
        Matcher matcher = RUN_PATTERN.matcher(task);
        return matcher.matches() ? matcher.group(2) : "";
    }

    default String getTaskFunction() {
        String task = getTask();
        Matcher matcher = RUN_PATTERN.matcher(task);
        return matcher.matches() ? matcher.group(3) : "";
    }

    default String getTaskFunctionVersion() {
        String task = getTask();
        Matcher matcher = RUN_PATTERN.matcher(task);
        return matcher.matches() ? matcher.group(4) : "";
    }
}
