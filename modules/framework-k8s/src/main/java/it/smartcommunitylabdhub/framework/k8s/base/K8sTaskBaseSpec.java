package it.smartcommunitylabdhub.framework.k8s.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.commons.models.entities.task.TaskBaseSpec;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreAffinity;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreEnv;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreLabel;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreNodeSelector;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreResource;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreToleration;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreVolume;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class K8sTaskBaseSpec extends TaskBaseSpec {

    private List<CoreVolume> volumes;

    @JsonProperty("node_selector")
    private List<CoreNodeSelector> nodeSelector;

    private List<CoreEnv> envs;

    private CoreResource resources;

    private Set<String> secrets;

    private CoreAffinity affinity;

    private List<CoreToleration> tolerations;

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        K8sTaskBaseSpec spec = mapper.convertValue(data, K8sTaskBaseSpec.class);

        this.volumes = spec.getVolumes();
        this.nodeSelector = spec.getNodeSelector();
        this.envs = spec.getEnvs();
        this.resources = spec.getResources();
        this.secrets = spec.getSecrets();
        this.affinity = spec.getAffinity();
        this.tolerations = spec.getTolerations();
    }
}
