package it.smartcommunitylabdhub.commons.models.entities.task;

import it.smartcommunitylabdhub.commons.models.base.BaseSpec;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class TaskBaseSpec extends BaseSpec {

    String function;

    @Override
    public void configure(Map<String, Serializable> data) {
        TaskBaseSpec concreteSpec = mapper.convertValue(data, TaskBaseSpec.class);

        this.setFunction(concreteSpec.getFunction());
    }
}
