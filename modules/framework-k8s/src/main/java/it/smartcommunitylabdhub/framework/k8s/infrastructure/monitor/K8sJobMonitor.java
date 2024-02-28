package it.smartcommunitylabdhub.framework.k8s.infrastructure.monitor;

import it.smartcommunitylabdhub.commons.annotations.infrastructure.MonitorComponent;

@MonitorComponent(framework = "job")
public class K8sJobMonitor implements K8sBaseMonitor<Void> {

    @Override
    public Void monitor() {
        System.out.println("Monitor Job");
        return null;
    }
}
