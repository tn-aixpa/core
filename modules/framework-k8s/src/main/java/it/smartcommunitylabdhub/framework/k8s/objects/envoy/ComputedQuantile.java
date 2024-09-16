package it.smartcommunitylabdhub.framework.k8s.objects.envoy;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

// Model for computed quantiles
public class ComputedQuantile {
    private String name;
    private List<QuantileValue> values;

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("values")
    public List<QuantileValue> getValues() {
        return values;
    }

    @JsonProperty("values")
    public void setValues(List<QuantileValue> values) {
        this.values = values;
    }
}