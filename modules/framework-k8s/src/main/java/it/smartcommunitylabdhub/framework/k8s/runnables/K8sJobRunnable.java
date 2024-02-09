package it.smartcommunitylabdhub.framework.k8s.runnables;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.commons.annotations.infrastructure.RunnableComponent;
import it.smartcommunitylabdhub.commons.infrastructure.factories.runnables.BaseRunnable;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreEnv;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreNodeSelector;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreResource;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreVolume;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.*;

@RunnableComponent(framework = "k8sjob")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class K8sJobRunnable extends BaseRunnable {

  private String runtime;

  private String task;

  private String image;

  private String command;

  private String state;

  private String[] args;

  private List<CoreEnv> envs;

  private List<CoreVolume> volumes;

  @JsonProperty("node_selector")
  private List<CoreNodeSelector> nodeSelector;

  private List<CoreResource> resources;

  // mapping secret name to the list of keys to of the secret to use
  private Map<String, Set<String>> secrets;

  @Override
  public String getFramework() {
    return "k8sjob";
  }
}
