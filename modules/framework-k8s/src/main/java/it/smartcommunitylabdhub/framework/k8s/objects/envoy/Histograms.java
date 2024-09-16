package it.smartcommunitylabdhub.framework.k8s.objects.envoy;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

// Model for histograms
public class Histograms {
    private List<Integer> supportedQuantiles;
    private List<ComputedQuantile> computedQuantiles;

    @JsonProperty("supported_quantiles")
    public List<Integer> getSupportedQuantiles() {
        return supportedQuantiles;
    }

    @JsonProperty("supported_quantiles")
    public void setSupportedQuantiles(List<Integer> supportedQuantiles) {
        this.supportedQuantiles = supportedQuantiles;
    }

    @JsonProperty("computed_quantiles")
    public List<ComputedQuantile> getComputedQuantiles() {
        return computedQuantiles;
    }

    @JsonProperty("computed_quantiles")
    public void setComputedQuantiles(List<ComputedQuantile> computedQuantiles) {
        this.computedQuantiles = computedQuantiles;
    }
}