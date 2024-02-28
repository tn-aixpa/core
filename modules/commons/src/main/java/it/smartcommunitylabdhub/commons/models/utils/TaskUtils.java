package it.smartcommunitylabdhub.commons.models.utils;

import it.smartcommunitylabdhub.commons.accessors.spec.TaskSpecAccessor;
import it.smartcommunitylabdhub.commons.models.entities.function.Function;
import it.smartcommunitylabdhub.commons.models.entities.workflow.Workflow;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaskUtils {

    private static final Pattern FUNCTION_PATTERN = Pattern.compile("([^:/]+)://([^/]+)/([^:]+):(.+)");

    private TaskUtils() {}

    //TODO this goes into the accessor, via a with()
    public static TaskSpecAccessor parseFunction(String taskString) {
        Matcher matcher = FUNCTION_PATTERN.matcher(taskString);
        if (matcher.matches()) {
            String runtime = matcher.group(1);
            String project = matcher.group(2);
            String function = matcher.group(3);
            String version = matcher.group(4);

            Map<String, String> map = new HashMap<>();
            map.put("runtime", runtime);
            map.put("project", project);
            map.put("function", function);
            map.put("version", version);

            return TaskSpecAccessor.with(map);
        }

        throw new IllegalArgumentException("Cannot create accessor for the given task string.");
    }

    public static String buildFunctionString(Function f) {
        return (f.getKind() + "://" + f.getProject() + "/" + f.getName() + ":" + f.getId());
    }

    public static String buildWorkflowString(Workflow w) {
        return (w.getKind() + "://" + w.getProject() + "/" + w.getName() + ":" + w.getId());
    }
}
