package it.smartcommunitylabdhub.framework.k8s.infrastructure.proxy.envoy.fields;

import lombok.Builder;
import lombok.Data;

// RouteAction class
@Data
@Builder
public class RouteAction {
    private String cluster;
}
