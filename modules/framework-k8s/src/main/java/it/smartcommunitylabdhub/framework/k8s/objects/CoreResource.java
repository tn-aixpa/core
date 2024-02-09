package it.smartcommunitylabdhub.framework.k8s.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public record CoreResource(
  @JsonProperty("resource_type") String resourceType,
  String requests,
  String limits
)
  implements Serializable {}
