package it.smartcommunitylabdhub.framework.k8s.runnables;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.commons.infrastructure.RunRunnable;
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
public class K8sRunnable implements RunRunnable {

    private String id;

    private String project;

    private String runtime;

    private String task;

    private String image;

    private String command;

    private String[] args;

    private List<CoreEnv> envs;

    // mapping secret name to the list of keys to of the secret to use
    private Map<String, Set<String>> secrets;

    private CoreResource resources;

    private List<CoreVolume> volumes;

    @JsonProperty("node_selector")
    private List<CoreNodeSelector> nodeSelector;

    private CoreAffinity affinity;

    private List<CoreToleration> tolerations;

    private List<CoreLabel> labels;

    private String state;

    @Override
    public String getFramework() {
        return "k8s";
    }
}
