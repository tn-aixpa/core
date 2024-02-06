package it.smartcommunitylabdhub.core.models.entities.task.specs;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.core.components.infrastructure.objects.CoreEnv;
import it.smartcommunitylabdhub.core.components.infrastructure.objects.CoreNodeSelector;
import it.smartcommunitylabdhub.core.components.infrastructure.objects.CoreResource;
import it.smartcommunitylabdhub.core.components.infrastructure.objects.CoreVolume;
import it.smartcommunitylabdhub.core.utils.jackson.JacksonMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class K8sTaskBaseSpec extends TaskBaseSpec {

    List<CoreVolume> volumes;

    @JsonProperty("node_selector")
    List<CoreNodeSelector> nodeSelector;

    List<CoreEnv> envs;

    List<CoreResource> resources;
    
    Set<String> secrets;

    @Override
    public void configure(Map<String, Object> data) {
        K8sTaskBaseSpec concreteSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
                data, K8sTaskBaseSpec.class);

        this.setVolumes(concreteSpec.getVolumes());
        this.setNodeSelector(concreteSpec.getNodeSelector());
        this.setEnvs(concreteSpec.getEnvs());
        this.setResources(concreteSpec.getResources());
        this.setSecrets(concreteSpec.getSecrets());
        super.configure(data);

        this.setExtraSpecs(concreteSpec.getExtraSpecs());
    }
}
