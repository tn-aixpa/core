package it.smartcommunitylabdhub.framework.k8s.infrastructure.proxy.envoy.fields;

import lombok.Builder;
import lombok.Data;

import java.util.List;

// HttpConnectionManager class
@Data
@Builder
public class HttpConnectionManager {
    private String statPrefix; // Use camelCase for consistency
    private List<HttpFilter> httpFilters; // Use camelCase for consistency
    private RouteConfig routeConfig;
}
