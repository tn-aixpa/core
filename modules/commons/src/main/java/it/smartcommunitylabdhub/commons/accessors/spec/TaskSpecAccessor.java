package it.smartcommunitylabdhub.commons.accessors.spec;

import it.smartcommunitylabdhub.commons.accessors.Accessor;
import java.io.Serializable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface TaskSpecAccessor extends Accessor<Serializable> {
    Pattern TASK_PATTERN = Pattern.compile("([^:/]+)://([^/]+)/([^:]+):(.+)");

    static TaskSpecAccessor with(Map<String, Serializable> map) {
        return () -> map;
    }

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

    // Extract individual parts of the task string
    default String getFunctionRuntime() {
        String task = getFunction();
        Matcher matcher = TASK_PATTERN.matcher(task);
        return matcher.matches() ? matcher.group(1) : null;
    }

    default String getFunctionProject() {
        String task = getFunction();
        Matcher matcher = TASK_PATTERN.matcher(task);
        return matcher.matches() ? matcher.group(2) : null;
    }

    default String getFunctionName() {
        String task = getFunction();
        Matcher matcher = TASK_PATTERN.matcher(task);
        return matcher.matches() ? matcher.group(3) : null;
    }

    default String getFunctionVersion() {
        String task = getFunction();
        Matcher matcher = TASK_PATTERN.matcher(task);
        return matcher.matches() ? matcher.group(4) : null;
    }
}
