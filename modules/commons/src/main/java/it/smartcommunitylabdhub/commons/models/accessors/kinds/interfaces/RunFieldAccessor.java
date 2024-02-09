package it.smartcommunitylabdhub.commons.models.accessors.kinds.interfaces;

import java.util.Map;

public interface RunFieldAccessor extends CommonFieldAccessor {
  default String getTask() {
    return (String) ((Map<String, Object>) getField("spec")).get("task");
  }

  static RunFieldAccessor with(Map<String, Object> map) {
    return () -> map;
  }
}
