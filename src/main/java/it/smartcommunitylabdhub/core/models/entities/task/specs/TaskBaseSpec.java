package it.smartcommunitylabdhub.core.models.entities.task.specs;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.core.models.base.specs.BaseSpec;
import it.smartcommunitylabdhub.core.utils.jackson.JacksonMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class TaskBaseSpec extends BaseSpec {
    String function;

    List<Map<String, Object>> volumes;

    @JsonProperty("volume_mounts")
    List<Map<String, Object>> volumeMounts;

    List<Map<String, Object>> env;

    Map<String, Object> resources;


    @Override
    public void configure(Map<String, Object> data) {
        TaskBaseSpec concreteSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
                data, TaskBaseSpec.class);

        this.setFunction(concreteSpec.getFunction());
        this.setVolumes(concreteSpec.getVolumes());
        this.setVolumeMounts(concreteSpec.getVolumeMounts());
        this.setEnv(concreteSpec.getEnv());
        this.setResources(concreteSpec.getResources());
        super.configure(data);

        this.setExtraSpecs(concreteSpec.getExtraSpecs());
    }
}
