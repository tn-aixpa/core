package it.smartcommunitylabdhub.framework.k8s.objects.envoy;

import com.fasterxml.jackson.annotation.JsonProperty;

// Model for histogram values
public class QuantileValue {
    private Double cumulative;
    private Double interval;

    @JsonProperty("cumulative")
    public Double getCumulative() {
        return cumulative;
    }

    @JsonProperty("cumulative")
    public void setCumulative(Double cumulative) {
        this.cumulative = cumulative;
    }

    @JsonProperty("interval")
    public Double getInterval() {
        return interval;
    }

    @JsonProperty("interval")
    public void setInterval(Double interval) {
        this.interval = interval;
    }
}