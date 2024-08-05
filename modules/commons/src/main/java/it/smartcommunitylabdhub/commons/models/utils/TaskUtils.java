package it.smartcommunitylabdhub.commons.models.utils;

import it.smartcommunitylabdhub.commons.Keys;
import it.smartcommunitylabdhub.commons.accessors.spec.TaskSpecAccessor;
import it.smartcommunitylabdhub.commons.models.base.Executable;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaskUtils {

    private static final Pattern FUNCTION_PATTERN = Pattern.compile(Keys.PATH_PATTERN);

    private static final int first = 1;
    private static final int second = 2;
    private static final int third = 3;
    private static final int fourth = 4;

    private TaskUtils() {}

    //TODO this goes into the accessor, via a with()
    public static TaskSpecAccessor parseFunction(String taskString) {
        Matcher matcher = FUNCTION_PATTERN.matcher(taskString);
        if (matcher.matches()) {
            String runtime = matcher.group(first);
            String project = matcher.group(second);
            String function = matcher.group(third);
            String version = matcher.group(fourth);

            Map<String, String> map = new HashMap<>();
            map.put("runtime", runtime);
            map.put("project", project);
            map.put("function", function);
            map.put("version", version);

            return TaskSpecAccessor.with(map);
        }

        throw new IllegalArgumentException("Cannot create accessor for the given task string.");
    }

    public static String buildString(Executable e) {
        return (e.getKind() + "://" + e.getProject() + "/" + e.getName() + ":" + e.getId());
    }
}
