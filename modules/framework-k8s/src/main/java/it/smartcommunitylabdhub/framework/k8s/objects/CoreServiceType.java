package it.smartcommunitylabdhub.framework.k8s.objects;

public enum CoreServiceType {
    ExternalName,
    ClusterIP,
    NodePort,
    LoadBalancer,
}
