package it.smartcommunitylabdhub.framework.k8s.runnables;

import it.smartcommunitylabdhub.commons.annotations.infrastructure.RunnableComponent;
import it.smartcommunitylabdhub.framework.k8s.objects.CorePort;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@RunnableComponent(framework = "k8sserve")
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class K8sServeRunnable extends K8sDeploymentRunnable {

    private List<CorePort> servicePorts;

    private String serviceType;

    @Override
    public String getFramework() {
        return "k8sserve";
    }
}
