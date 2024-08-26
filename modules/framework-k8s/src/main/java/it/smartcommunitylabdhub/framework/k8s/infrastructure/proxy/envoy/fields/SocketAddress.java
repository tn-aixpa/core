package it.smartcommunitylabdhub.framework.k8s.infrastructure.proxy.envoy.fields;

import lombok.Builder;
import lombok.Data;

// SocketAddress class
@Data
@Builder
public class SocketAddress {
    private String address;
    private int portValue; // Use camelCase for consistency
}
