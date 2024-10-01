package it.smartcommunitylabdhub.framework.k8s.objects.envoy.collectors.envoy;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EnvoyStatData implements Serializable {

    @JsonProperty("total_requests")
    private Long totalRequests;

    @JsonProperty("current_requests")
    private Long windowRequests;

    private Long request2xx;
    private Long request3xx;
    private Long request4xx;
    private Long request5xx;
    private String timestamp;
    @JsonProperty("inactivity_time")
    private Long inactivityTime;
}