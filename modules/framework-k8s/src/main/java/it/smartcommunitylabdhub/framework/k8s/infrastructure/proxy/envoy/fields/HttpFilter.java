package it.smartcommunitylabdhub.framework.k8s.infrastructure.proxy.envoy.fields;

import lombok.Builder;
import lombok.Data;

// HttpFilter class
@Data
@Builder
public class HttpFilter {
    private String name;
    private TypedConfig typedConfig; // Could be specific config or general
}
