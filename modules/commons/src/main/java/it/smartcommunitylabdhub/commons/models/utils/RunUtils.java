package it.smartcommunitylabdhub.commons.models.utils;

import it.smartcommunitylabdhub.commons.accessors.spec.RunSpecAccessor;
import it.smartcommunitylabdhub.commons.exceptions.CoreException;
import it.smartcommunitylabdhub.commons.models.base.BaseEntity;
import it.smartcommunitylabdhub.commons.models.entities.function.Function;
import it.smartcommunitylabdhub.commons.models.entities.task.Task;
import it.smartcommunitylabdhub.commons.models.entities.workflow.Workflow;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.http.HttpStatus;

//TODO remove
@Deprecated(forRemoval = true)
public class RunUtils {

    public static final Pattern RUN_PATTERN = Pattern.compile("([^:/]+)://([^/]+)/([^/]+):(.+)");

    private RunUtils() {}

    //TODO this goes into the accessor, via a with()
    public static RunSpecAccessor parseRun(String value) {
        Matcher matcher = RUN_PATTERN.matcher(value);
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
        throw new CoreException(
            "InvalidRunStringCase",
            "Cannot create accessor for the given Run string.",
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    //TODO this should be removed, core shouldn't need this info
    public static <T extends BaseEntity> String buildRunString(T type, Task task) {
        if (type instanceof Function) {
            Function f = (Function) type;
            return (task.getKind() + "://" + f.getProject() + "/" + f.getName() + ":" + f.getId());
        } else if (type instanceof Workflow) {
            Workflow w = (Workflow) type;
            return (task.getKind() + "://" + w.getProject() + "/" + w.getName() + ":" + w.getId());
        } else {
            throw new CoreException(
                "CannotComposeRunField",
                "Cannot compose Run field for the given object.",
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
