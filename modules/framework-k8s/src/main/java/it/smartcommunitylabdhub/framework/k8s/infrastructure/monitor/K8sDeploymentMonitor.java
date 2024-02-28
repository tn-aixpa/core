package it.smartcommunitylabdhub.framework.k8s.infrastructure.monitor;

import it.smartcommunitylabdhub.commons.annotations.infrastructure.MonitorComponent;

@MonitorComponent(framework = "deployment")
public class K8sDeploymentMonitor implements K8sBaseMonitor<Void> {

    @Override
    public Void monitor() {
        System.out.println("Monitor Deployment");
        return null;
    }
}
