package it.smartcommunitylabdhub.framework.k8s.base;

import com.fasterxml.jackson.annotation.JsonProperty;

import it.smartcommunitylabdhub.commons.jackson.JacksonMapper;
import it.smartcommunitylabdhub.commons.models.entities.task.TaskBaseSpec;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreAffinity;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreEnv;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreLabel;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreNodeSelector;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreResource;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreToleration;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreVolume;
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

    private List<CoreResource> resources;

    private Set<String> secrets;

    private CoreAffinity affinity;

    private List<CoreToleration> tolerations;

    private List<CoreLabel> labels;


    @Override
    public void configure(Map<String, Object> data) {
        K8sTaskBaseSpec concreteSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(data, K8sTaskBaseSpec.class);

        this.setVolumes(concreteSpec.getVolumes());
        this.setNodeSelector(concreteSpec.getNodeSelector());
        this.setEnvs(concreteSpec.getEnvs());
        this.setResources(concreteSpec.getResources());
        this.setSecrets(concreteSpec.getSecrets());
        this.setAffinity(concreteSpec.getAffinity());
        this.setLabels(concreteSpec.getLabels());
        this.setTolerations(concreteSpec.getTolerations());
        super.configure(data);

        this.setExtraSpecs(concreteSpec.getExtraSpecs());
    }
}
