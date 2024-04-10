package it.smartcommunitylabdhub.commons.models.entities.task;

import it.smartcommunitylabdhub.commons.models.base.BaseSpec;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TaskBaseSpec extends BaseSpec {

    String function;

    @Override
    public void configure(Map<String, Serializable> data) {
        TaskBaseSpec spec = mapper.convertValue(data, TaskBaseSpec.class);

        this.function = spec.getFunction();
    }

    public static TaskBaseSpec from(Map<String, Serializable> map) {
        TaskBaseSpec spec = new TaskBaseSpec();
        spec.configure(map);

        return spec;
    }
}
