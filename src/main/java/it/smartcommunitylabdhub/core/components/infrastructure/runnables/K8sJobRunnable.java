package it.smartcommunitylabdhub.core.components.infrastructure.runnables;

import it.smartcommunitylabdhub.core.annotations.infrastructure.RunnableComponent;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.runnables.BaseRunnable;
import it.smartcommunitylabdhub.core.components.infrastructure.objects.CoreEnv;
import it.smartcommunitylabdhub.core.components.infrastructure.objects.CoreNodeSelector;
import it.smartcommunitylabdhub.core.components.infrastructure.objects.CoreResource;
import it.smartcommunitylabdhub.core.components.infrastructure.objects.CoreVolume;
import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;


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

    List<CoreVolume> volumes;

    @JsonProperty("node_selector")
    List<CoreNodeSelector> nodeSelector;


    List<CoreResource> resources;
    
    // mapping secret name to the list of keys to of the secret to use
    Map<String, Set<String>> secrets;


    @Override
    public String getFramework() {
        return "k8sjob";
    }

}
