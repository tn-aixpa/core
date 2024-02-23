package it.smartcommunitylabdhub.framework.k8s.runnables;

import it.smartcommunitylabdhub.commons.annotations.infrastructure.RunnableComponent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@RunnableComponent(framework = "k8sdeployment")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class K8sDeploymentRunnable extends K8sRunnable {

    @Override
    public String getFramework() {
        return "k8sdeployment";
    }
}
