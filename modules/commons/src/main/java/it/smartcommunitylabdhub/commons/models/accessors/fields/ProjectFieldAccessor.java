package it.smartcommunitylabdhub.commons.models.accessors.fields;

import java.util.Map;

public interface ProjectFieldAccessor extends CommonFieldAccessor {
  default String getName() {
    return mapHasField(getMetadata(), "name")
      ? (String) getMetadata().get("name")
      : null;
  }

  static ProjectFieldAccessor with(Map<String, Object> map) {
    return () -> map;
  }
}
