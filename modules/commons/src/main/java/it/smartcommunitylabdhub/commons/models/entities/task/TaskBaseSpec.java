package it.smartcommunitylabdhub.commons.models.entities.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.commons.models.base.BaseSpec;
import jakarta.validation.constraints.NotEmpty;
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

    @NotEmpty
    @JsonProperty("function_id")
    private String functionId;

    @Override
    public void configure(Map<String, Serializable> data) {
        TaskBaseSpec concreteSpec = mapper.convertValue(data, TaskBaseSpec.class);

        this.setFunction(concreteSpec.getFunction());
        this.setFunctionId(concreteSpec.getFunctionId());
    }
}
