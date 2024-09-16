package it.smartcommunitylabdhub.framework.k8s.objects.envoy;

import java.io.Serializable;

// Model for stat
public class Stat implements Serializable {
    private String name;
    private Long value; // Integer to handle null as well
    private Histograms histograms;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public Histograms getHistograms() {
        return histograms;
    }

    public void setHistograms(Histograms histograms) {
        this.histograms = histograms;
    }
}
