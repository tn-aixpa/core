package it.smartcommunitylabdhub.framework.k8s.infrastructure.proxy.envoy.fields;

import lombok.Builder;
import lombok.Data;

import java.util.List;

// RouteConfig class
@Data
@Builder
public class RouteConfig {
    private String name;
    private List<VirtualHost> virtualHosts; // Use camelCase for consistency
}
