package it.smartcommunitylabdhub.framework.k8s.infrastructure.proxy.envoy.fields;

import lombok.Builder;
import lombok.Data;

import java.util.List;

// LoadAssignment class
@Data
@Builder
public class LoadAssignment {
    private String clusterName; // Use camelCase for consistency
    private List<Endpoint> endpoints;
}
