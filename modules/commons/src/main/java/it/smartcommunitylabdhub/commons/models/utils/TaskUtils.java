package it.smartcommunitylabdhub.commons.models.utils;

import it.smartcommunitylabdhub.commons.accessors.spec.TaskSpecAccessor;
import it.smartcommunitylabdhub.commons.models.base.BaseDTO;
import it.smartcommunitylabdhub.commons.models.entities.function.Function;
import it.smartcommunitylabdhub.commons.models.entities.workflow.Workflow;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//TODO remove
@Deprecated(forRemoval = true)
public class TaskUtils {

    private static final Pattern TASK_PATTERN = Pattern.compile("([^:/]+)://([^/]+)/([^:]+):(.+)");

    private TaskUtils() {}

    //TODO this goes into the accessor, via a with()
    public static TaskSpecAccessor parseTask(String taskString) {
        Matcher matcher = TASK_PATTERN.matcher(taskString);
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

    //TODO this should be removed, core shouldn't need this info
    public static <T extends BaseDTO> String buildTaskString(T type) {
        if (type instanceof Function) {
            Function f = (Function) type;
            return (f.getKind() + "://" + f.getProject() + "/" + f.getName() + ":" + f.getId());
        } else if (type instanceof Workflow) {
            Workflow w = (Workflow) type;
            return (w.getKind() + "://" + w.getProject() + "/" + w.getName() + ":" + w.getId());
        } else {
            throw new IllegalArgumentException("Cannot compose task field for the given object.");
        }
    }
}
