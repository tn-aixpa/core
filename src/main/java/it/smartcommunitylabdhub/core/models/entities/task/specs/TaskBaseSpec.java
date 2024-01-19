package it.smartcommunitylabdhub.core.models.entities.task.specs;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.core.models.base.specs.BaseSpec;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class TaskBaseSpec<S extends TaskBaseSpec<S>> extends BaseSpec<S> {
    String function;

    List<Map<String, Object>> volumes;

    @JsonProperty("volume_mounts")
    List<Map<String, Object>> volumeMounts;

    List<Map<String, Object>> env;
    
    Map<String, Object> resources;


    @Override
    protected void configureSpec(S concreteSpec) {
        this.setFunction(concreteSpec.getFunction());
        this.setVolumes(concreteSpec.getVolumes());
        this.setVolumeMounts(concreteSpec.getVolumeMounts());
        this.setEnv(concreteSpec.getEnv());
        this.setResources(concreteSpec.getResources());
        this.setExtraSpecs(concreteSpec.getExtraSpecs());
    }
}
