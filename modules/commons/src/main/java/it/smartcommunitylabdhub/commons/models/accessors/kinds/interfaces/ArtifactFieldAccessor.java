package it.smartcommunitylabdhub.commons.models.accessors.kinds.interfaces;

import java.util.Map;

public interface ArtifactFieldAccessor extends CommonFieldAccessor {
  static ArtifactFieldAccessor with(Map<String, Object> map) {
    return () -> map;
  }
}
