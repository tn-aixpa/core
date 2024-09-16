package it.smartcommunitylabdhub.framework.k8s.objects.envoy;

import java.io.Serializable;
import java.util.List;

// Model for the response
public class CoreStat implements Serializable {
    private List<Stat> stats;

    // Getters and Setters
    public List<Stat> getStats() {
        return stats;
    }

    public void setStats(List<Stat> stats) {
        this.stats = stats;
    }
}