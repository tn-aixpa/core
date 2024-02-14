package it.smartcommunitylabdhub.framework.k8s.runnables;

import it.smartcommunitylabdhub.commons.annotations.infrastructure.RunnableComponent;
import lombok.*;
import lombok.experimental.SuperBuilder;

@RunnableComponent(framework = "k8sserve")
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class K8sServeRunnable extends K8SRunnable {

    private String entrypoint;

    private String state;

    private String[] args;

    @Override
    public String getFramework() {
        return "k8sserve";
    }
}
