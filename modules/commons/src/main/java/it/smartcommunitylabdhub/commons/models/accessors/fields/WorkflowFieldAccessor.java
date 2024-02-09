package it.smartcommunitylabdhub.commons.models.accessors.fields;

import java.util.Map;

public interface WorkflowFieldAccessor extends CommonFieldAccessor {
  static WorkflowFieldAccessor with(Map<String, Object> map) {
    return () -> map;
  }
}
