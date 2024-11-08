package it.smartcommunitylabdhub.framework.argo.runnables;

import it.smartcommunitylabdhub.commons.annotations.infrastructure.RunnableComponent;
import it.smartcommunitylabdhub.framework.argo.infrastructure.k8s.K8sArgoCronWorkflowFramework;
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
public class K8sArgoCronWorkflowRunnable extends K8sArgoWorkflowRunnable {

    private String schedule;

    @Override
    public String getFramework() {
        return K8sArgoCronWorkflowFramework.FRAMEWORK;
    }
}
