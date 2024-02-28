package it.smartcommunitylabdhub.framework.k8s.infrastructure.monitor;

@FunctionalInterface
public interface K8sBaseMonitor<T> {
    T monitor();
}
