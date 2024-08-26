package it.smartcommunitylabdhub.framework.k8s.infrastructure.proxy.envoy.fields;

import lombok.Builder;
import lombok.Data;

import java.util.List;

// VirtualHost class
@Data
@Builder
public class VirtualHost {
    private String name;
    private List<String> domains;
    private List<Route> routes;
}
