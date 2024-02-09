package it.smartcommunitylabdhub.framework.k8s.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Map;

public record CoreVolume(
  @JsonProperty("volume_type") String volumeType,
  @JsonProperty("mount_path") String mountPath,
  String name,
  Map<String, Object> spec
)
  implements Serializable {}
