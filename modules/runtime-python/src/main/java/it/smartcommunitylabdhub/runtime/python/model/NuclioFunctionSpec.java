package it.smartcommunitylabdhub.runtime.python.model;

import java.io.Serializable;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class NuclioFunctionSpec {

    private String runtime;
    private String handler;
    private Serializable event;
    private Map<String, Serializable> triggers;

    private Integer minReplicas;
    private Integer maxReplicas;
}
