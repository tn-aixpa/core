package it.smartcommunitylabdhub.commons.models.accessors.kinds.interfaces;

import java.util.List;
import java.util.Map;

public interface DataItemFieldAccessor extends CommonFieldAccessor {
  @SuppressWarnings("unchecked")
  default Map<String, String> getLabels() {
    return mapHasField(getMetadata(), "labels")
      ? (Map<String, String>) getMetadata().get("labels")
      : null;
  }

  @SuppressWarnings("unchecked")
  default List<String> getAnnotations() {
    return mapHasField(getMetadata(), "annotations")
      ? (List<String>) getMetadata().get("annotations")
      : null;
  }

  default String getKey() {
    return mapHasField(getMetadata(), "key")
      ? (String) getMetadata().get("key")
      : null;
  }

  default String getTree() {
    return mapHasField(getMetadata(), "tree")
      ? (String) getMetadata().get("tree")
      : null;
  }

  default Integer getIter() {
    return mapHasField(getMetadata(), "iter")
      ? (Integer) getMetadata().get("iter")
      : null;
  }

  default String getProject() {
    return mapHasField(getMetadata(), "project")
      ? (String) getMetadata().get("project")
      : null;
  }

  static DataItemFieldAccessor with(Map<String, Object> map) {
    return () -> map;
  }
}
