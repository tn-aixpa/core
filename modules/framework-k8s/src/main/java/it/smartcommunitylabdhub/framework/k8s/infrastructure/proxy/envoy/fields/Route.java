package it.smartcommunitylabdhub.framework.k8s.infrastructure.proxy.envoy.fields;

import lombok.Builder;
import lombok.Data;

// Route class
@Data
@Builder
public class Route {
    private String name;
    private Match match;
    private RouteAction routeAction; // Use camelCase for consistency
}
