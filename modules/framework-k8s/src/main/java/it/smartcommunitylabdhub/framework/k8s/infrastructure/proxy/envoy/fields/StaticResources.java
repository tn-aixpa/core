package it.smartcommunitylabdhub.framework.k8s.infrastructure.proxy.envoy.fields;

import lombok.Builder;
import lombok.Data;

import java.util.List;

// Root class
@Data
@Builder
public class StaticResources {
    private List<Listener> listeners;
    private List<Cluster> clusters;
}
