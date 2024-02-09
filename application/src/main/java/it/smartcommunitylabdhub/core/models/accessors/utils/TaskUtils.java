package it.smartcommunitylabdhub.core.models.accessors.utils;

import it.smartcommunitylabdhub.commons.exceptions.CoreException;
import it.smartcommunitylabdhub.commons.models.base.interfaces.BaseEntity;
import it.smartcommunitylabdhub.commons.models.entities.function.Function;
import it.smartcommunitylabdhub.commons.models.entities.workflow.Workflow;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.http.HttpStatus;

public class TaskUtils {

  private static final Pattern TASK_PATTERN = Pattern.compile(
    "([^:/]+)://([^/]+)/([^:]+):(.+)"
  );

  private TaskUtils() {}

  public static TaskAccessor parseTask(String taskString) {
    Matcher matcher = TASK_PATTERN.matcher(taskString);
    if (matcher.matches()) {
      String kind = matcher.group(1);
      String project = matcher.group(2);
      String function = matcher.group(3);
      String version = matcher.group(4);

      return new TaskAccessor(kind, project, function, version);
    }
    throw new CoreException(
      "InvalidTaskStringCase",
      "Cannot create accessor for the given task string.",
      HttpStatus.INTERNAL_SERVER_ERROR
    );
  }

  public static <T extends BaseEntity> String buildTaskString(T type) {
    if (type instanceof Function) {
      Function f = (Function) type;
      return (
        f.getKind() +
        "://" +
        f.getProject() +
        "/" +
        f.getName() +
        ":" +
        f.getId()
      );
    } else if (type instanceof Workflow) {
      Workflow w = (Workflow) type;
      return (
        w.getKind() +
        "://" +
        w.getProject() +
        "/" +
        w.getName() +
        ":" +
        w.getId()
      );
    } else {
      throw new CoreException(
        "CannotComposeTaskField",
        "Cannot compose task field for the given object.",
        HttpStatus.INTERNAL_SERVER_ERROR
      );
    }
  }
}
