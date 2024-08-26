package it.smartcommunitylabdhub.framework.k8s.infrastructure.proxy.envoy.fields;

import lombok.Builder;
import lombok.Data;

import java.util.List;

// FilterChain class
@Data
@Builder
public class FilterChain {
    private List<Filter> filters;
}
