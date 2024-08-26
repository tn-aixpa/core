package it.smartcommunitylabdhub.framework.k8s.infrastructure.proxy.envoy.fields;

import lombok.Builder;
import lombok.Data;

// Address class
@Data
@Builder
public class Address {
    private SocketAddress socketAddress; // Use camelCase for consistency
}
