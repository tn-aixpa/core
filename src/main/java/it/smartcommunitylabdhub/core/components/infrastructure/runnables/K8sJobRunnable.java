package it.smartcommunitylabdhub.core.components.infrastructure.runnables;

import it.smartcommunitylabdhub.core.annotations.infrastructure.RunnableComponent;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.runnables.BaseRunnable;
import lombok.*;

import java.util.Map;


@RunnableComponent(framework = "k8sjob")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class K8sJobRunnable extends BaseRunnable {

    String runtime;

    String task;

    String image;

    String command;

    String state;

    String[] args;

    Map<String, String> envs;

    @Override
    public String framework() {
        return "k8sjob";
    }

}
