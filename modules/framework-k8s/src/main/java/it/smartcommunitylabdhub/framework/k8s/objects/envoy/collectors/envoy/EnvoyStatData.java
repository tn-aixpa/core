package it.smartcommunitylabdhub.framework.k8s.objects.envoy.collectors.envoy;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EnvoyStatData implements Serializable {

    @JsonProperty("total_requests")
    private Long totalRequests;
    private Long currentRequests;

    private Long request2xx;
    private Long request3xx;
    private Long request4xx;
    private Long request5xx;
}