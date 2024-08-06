package it.smartcommunitylabdhub.commons.models.utils;

import it.smartcommunitylabdhub.commons.Keys;
import it.smartcommunitylabdhub.commons.accessors.spec.RunSpecAccessor;
import it.smartcommunitylabdhub.commons.models.entities.task.Task;
import it.smartcommunitylabdhub.commons.models.entities.task.TaskBaseSpec;
import jakarta.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RunUtils {

    public static final Pattern TASK_PATTERN = Pattern.compile(Keys.PATH_PATTERN);

    private static final int first = 1;
    private static final int second = 2;
    private static final int third = 3;
    private static final int fourth = 4;

    private RunUtils() {}

    //TODO this goes into the accessor, via a with()
    public static RunSpecAccessor parseTask(String value) {
        Matcher matcher = TASK_PATTERN.matcher(value);
        if (matcher.matches()) {
            String task = matcher.group(first);
            String project = matcher.group(second);
            String function = matcher.group(third);
            String version = matcher.group(fourth);

            Map<String, String> map = new HashMap<>();
            map.put("task", task);
            map.put("project", project);
            map.put("function", function);
            map.put("version", version);
            map.put("runtime", task.indexOf('+') > 0 ? task.substring(0, task.indexOf('+')) : null);

            return RunSpecAccessor.with(map);
        }

        throw new IllegalArgumentException("Cannot create accessor for the given Run string.");
    }

    public static String buildTaskString(@NotNull Task task) {
        TaskBaseSpec taskSpec = new TaskBaseSpec();
        taskSpec.configure(task.getSpec());

        String function = taskSpec.getFunction();

        //run task matches function in task except for identifier
        Matcher matcher = TASK_PATTERN.matcher(function);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("invalid or missing function in task spec");
        }

        return (
            task.getKind() + "://" + matcher.group(second) + "/" + matcher.group(third) + ":" + matcher.group(fourth)
        );
    }
}
