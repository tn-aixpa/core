package it.smartcommunitylabdhub.framework.k8s.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public record CorePort(Integer port, @JsonProperty("target_port") Integer targetPort) implements Serializable {
}
