package it.smartcommunitylabdhub.framework.k8s.runnables;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.commons.infrastructure.factories.runnables.BaseRunnable;
import it.smartcommunitylabdhub.framework.k8s.objects.*;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class K8sRunnable extends BaseRunnable {

    private String runtime;

    private String task;

    private String image;

    private List<CoreEnv> envs;

    private List<CoreVolume> volumes;

    @JsonProperty("node_selector")
    private List<CoreNodeSelector> nodeSelector;

    private List<CoreResource> resources;

    // mapping secret name to the list of keys to of the secret to use
    private Map<String, Set<String>> secrets;

    private CoreAffinity affinity;

    private List<CoreToleration> tolerations;

    private List<CoreLabel> labels;

    private String state;

}
