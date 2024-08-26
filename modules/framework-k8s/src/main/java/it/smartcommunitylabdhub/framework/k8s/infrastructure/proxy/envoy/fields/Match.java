package it.smartcommunitylabdhub.framework.k8s.infrastructure.proxy.envoy.fields;

import lombok.Builder;
import lombok.Data;

// Match class
@Data
@Builder
public class Match {
    private String prefix;
}
