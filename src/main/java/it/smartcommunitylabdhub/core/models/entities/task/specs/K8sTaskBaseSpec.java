package it.smartcommunitylabdhub.core.models.entities.task.specs;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.core.utils.jackson.JacksonMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class K8sTaskBaseSpec extends TaskBaseSpec {

    List<Map<String, Object>> volumes;

    @JsonProperty("node_selector")
    Map<String, Object> nodeSelector;

    List<Map<String, Object>> envs;

    Map<String, Object> resources;


    @Override
    public void configure(Map<String, Object> data) {
        K8sTaskBaseSpec concreteSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
                data, K8sTaskBaseSpec.class);
        
        this.setVolumes(concreteSpec.getVolumes());
        this.setNodeSelector(concreteSpec.getNodeSelector());
        this.setEnvs(concreteSpec.getEnvs());
        this.setResources(concreteSpec.getResources());
        super.configure(data);

        this.setExtraSpecs(concreteSpec.getExtraSpecs());
    }
}
