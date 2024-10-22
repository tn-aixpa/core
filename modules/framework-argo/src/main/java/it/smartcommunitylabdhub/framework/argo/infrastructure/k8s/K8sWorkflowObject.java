package it.smartcommunitylabdhub.framework.argo.infrastructure.k8s;

import java.io.Serializable;

import io.argoproj.workflow.models.Workflow;
import io.kubernetes.client.common.KubernetesObject;
import io.kubernetes.client.openapi.models.V1ObjectMeta;

public class K8sWorkflowObject implements KubernetesObject, Serializable {
    private Workflow workflow;

    public K8sWorkflowObject(Workflow workflow) {
        this.workflow = workflow;
    }

    @Override
    public String getApiVersion() {
        return workflow.getApiVersion();
    }

    @Override
    public String getKind() {
        return workflow.getKind();
    }

    @Override
    public V1ObjectMeta getMetadata() {
        return workflow.getMetadata();
    }

    public Workflow getWorkflow() {
        return workflow;
    }
}