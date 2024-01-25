package it.smartcommunitylabdhub.core.models.entities.task.specs;

import it.smartcommunitylabdhub.core.models.base.specs.BaseSpec;
import it.smartcommunitylabdhub.core.utils.jackson.JacksonMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class TaskBaseSpec extends BaseSpec {

    String function;

    @Override
    public void configure(Map<String, Object> data) {
        TaskBaseSpec concreteSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
                data, TaskBaseSpec.class);

        this.setFunction(concreteSpec.getFunction());

        super.configure(data);

        this.setExtraSpecs(concreteSpec.getExtraSpecs());
    }

}
