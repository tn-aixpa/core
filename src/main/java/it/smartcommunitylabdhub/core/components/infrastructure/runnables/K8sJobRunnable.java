package it.smartcommunitylabdhub.core.components.infrastructure.runnables;

import it.smartcommunitylabdhub.core.annotations.infrastructure.RunnableComponent;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.runnables.BaseRunnable;
import it.smartcommunitylabdhub.core.components.infrastructure.objects.CoreEnv;
import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.Set;


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

    List<CoreEnv> envs;

    // mapping secret name to the list of keys to of the secret to use
    Map<String, Set<String>> secrets;

    List<Map<String, Object>> volumes;

    Map<String, String> nodeSelector;

    /**
     * K8S resource requests: <resource>:<value>
     */
    Map<String, String> requests;
    /**
     * K8S resource limits: <resource>:<value>
     */
    Map<String, String> limits;

    @Override
    public String getFramework() {
        return "k8sjob";
    }

}
