package it.smartcommunitylabdhub.commons.models.entities.task;

import it.smartcommunitylabdhub.commons.jackson.JacksonMapper;
import it.smartcommunitylabdhub.commons.models.base.BaseSpec;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskBaseSpec extends BaseSpec {

    String function;

    @Override
    public void configure(Map<String, Object> data) {
        TaskBaseSpec concreteSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(data, TaskBaseSpec.class);

        this.setFunction(concreteSpec.getFunction());

        super.configure(data);

        this.setExtraSpecs(concreteSpec.getExtraSpecs());
    }
}
