package it.smartcommunitylabdhub.framework.k8s.infrastructure.proxy.envoy.fields;

import lombok.Builder;
import lombok.Data;

// Filter class
@Data
@Builder
public class Filter {
    private String name;
    private TypedConfig typedConfig; // Use camelCase for consistency
}
