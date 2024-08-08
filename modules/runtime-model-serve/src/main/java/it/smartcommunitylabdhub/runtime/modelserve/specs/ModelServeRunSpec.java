package it.smartcommunitylabdhub.runtime.modelserve.specs;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseSpec;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ModelServeRunSpec extends RunBaseSpec {

    @JsonUnwrapped
    private ModelServeServeTaskSpec taskServeSpec;


    public ModelServeRunSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        ModelServeRunSpec spec = mapper.convertValue(data, ModelServeRunSpec.class);

        this.taskServeSpec = spec.getTaskServeSpec();

    }

    public void setTaskServeSpec(ModelServeServeTaskSpec taskServeSpec) {
        this.taskServeSpec = taskServeSpec;
    }
}
