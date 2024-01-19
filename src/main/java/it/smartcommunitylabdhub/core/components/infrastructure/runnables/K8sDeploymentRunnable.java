package it.smartcommunitylabdhub.core.components.infrastructure.runnables;

import it.smartcommunitylabdhub.core.annotations.infrastructure.RunnableComponent;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.runnables.BaseRunnable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;


@RunnableComponent(framework = "k8sdeployment")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class K8sDeploymentRunnable extends BaseRunnable {

    String runtime;

    String task;

    String image;

    String entrypoint;

    String state;

    String[] args;

    Map<String, String> envs;

    @Override
    public String framework() {
        return "k8sdeployment";
    }

}
