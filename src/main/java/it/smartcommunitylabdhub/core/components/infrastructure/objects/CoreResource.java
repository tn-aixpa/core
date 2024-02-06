package it.smartcommunitylabdhub.core.components.infrastructure.objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CoreResource(@JsonProperty("resource_type") String resourceType, String requests, String limits) {
    
}
