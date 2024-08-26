package it.smartcommunitylabdhub.framework.k8s.infrastructure.proxy.envoy.fields;

import lombok.Builder;
import lombok.Data;

// Cluster class
@Data
@Builder
public class Cluster {
    private String name;
    private String type;
    private String lbPolicy; // Use camelCase for consistency
    private LoadAssignment loadAssignment; // Use camelCase for consistency
}
