package it.smartcommunitylabdhub.core.components.infrastructure.objects;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CoreVolume(@JsonProperty("volume_type") String volumeType, @JsonProperty("mount_path") String mountPath, String name, Map<String, Object> spec) {}
