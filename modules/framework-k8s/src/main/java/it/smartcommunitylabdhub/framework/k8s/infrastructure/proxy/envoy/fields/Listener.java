package it.smartcommunitylabdhub.framework.k8s.infrastructure.proxy.envoy.fields;

import lombok.Builder;
import lombok.Data;

import java.util.List;

// Listener class
@Data
@Builder
public class Listener {
    private String name;
    private Address address;
    private List<FilterChain> filterChains; // Use camelCase for consistency
}
