package it.smartcommunitylabdhub.framework.argo.runnables;

import java.util.Map;
import java.io.Serializable;

import it.smartcommunitylabdhub.commons.annotations.infrastructure.RunnableComponent;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sRunnable;
import it.smartcommunitylabdhub.framework.argo.infrastructure.k8s.K8sArgoWorkflowFramework;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@RunnableComponent(framework = K8sArgoWorkflowFramework.FRAMEWORK)
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class K8sArgoWorkflowRunnable extends K8sRunnable {

    private String workflowSpec;

    private Map<String, Serializable> parameters;

    @Override
    public String getFramework() {
        return K8sArgoWorkflowFramework.FRAMEWORK;
    }
}
