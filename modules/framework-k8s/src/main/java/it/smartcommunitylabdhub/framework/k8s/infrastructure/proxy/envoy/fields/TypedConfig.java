package it.smartcommunitylabdhub.framework.k8s.infrastructure.proxy.envoy.fields;

import lombok.Builder;
import lombok.Data;

// TypedConfig class (generic configuration)
@Data
@Builder
public class TypedConfig {
    private String type;
    private HttpConnectionManager httpConnectionManager; // Specific configuration for HTTP
    private Router router; // Specific configuration for Router
}
