package it.smartcommunitylabdhub.framework.argo.objects;

import java.io.Serializable;

import io.argoproj.workflow.models.CronWorkflow;
import io.kubernetes.client.common.KubernetesObject;
import io.kubernetes.client.openapi.models.V1ObjectMeta;

public class K8sCronWorkflowObject implements KubernetesObject, Serializable {
    private CronWorkflow workflow;

    public K8sCronWorkflowObject(CronWorkflow workflow) {
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

    public CronWorkflow getWorkflow() {
        return workflow;
    }
}