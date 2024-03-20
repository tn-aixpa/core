package it.smartcommunitylabdhub.commons.models.utils;

import it.smartcommunitylabdhub.commons.Keys;
import it.smartcommunitylabdhub.commons.accessors.spec.RunSpecAccessor;
import it.smartcommunitylabdhub.commons.models.entities.task.Task;
import it.smartcommunitylabdhub.commons.models.entities.task.TaskBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.workflow.Workflow;
import jakarta.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RunUtils {

    public static final Pattern TASK_PATTERN = Pattern.compile(Keys.PATH_PATTERN);

    private RunUtils() {}

    //TODO this goes into the accessor, via a with()
    public static RunSpecAccessor parseTask(String value) {
        Matcher matcher = TASK_PATTERN.matcher(value);
        if (matcher.matches()) {
            String task = matcher.group(1);
            String project = matcher.group(2);
            String function = matcher.group(3);
            String version = matcher.group(4);

            Map<String, String> map = new HashMap<>();
            map.put("task", task);
            map.put("project", project);
            map.put("function", function);
            map.put("version", version);

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

        return (task.getKind() + "://" + matcher.group(2) + "/" + matcher.group(3) + ":" + matcher.group(4));
    }

    public static String buildWorkflowString(Workflow w, Task task) {
        return (task.getKind() + "://" + w.getProject() + "/" + w.getName() + ":" + w.getId());
    }
}
