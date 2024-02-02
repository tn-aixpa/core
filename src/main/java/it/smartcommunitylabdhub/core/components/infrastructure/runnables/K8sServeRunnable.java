package it.smartcommunitylabdhub.core.components.infrastructure.runnables;

import it.smartcommunitylabdhub.core.annotations.infrastructure.RunnableComponent;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.runnables.BaseRunnable;
import lombok.*;

import java.util.Map;


@RunnableComponent(framework = "k8sserve")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class K8sServeRunnable extends BaseRunnable {

    String runtime;

    String task;

    String image;

    String entrypoint;

    String state;

    String[] args;

    Map<String, String> envs;

    @Override
    public String getFramework() {
        return "k8sserve";
    }

}
