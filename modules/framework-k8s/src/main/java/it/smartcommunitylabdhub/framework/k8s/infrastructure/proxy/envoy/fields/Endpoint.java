package it.smartcommunitylabdhub.framework.k8s.infrastructure.proxy.envoy.fields;

import lombok.Builder;
import lombok.Data;

// Endpoint class
@Data
@Builder
public class Endpoint {
    private Address address;
}
