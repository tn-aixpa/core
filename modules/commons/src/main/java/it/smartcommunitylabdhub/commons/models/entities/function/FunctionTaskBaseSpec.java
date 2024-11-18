package it.smartcommunitylabdhub.commons.models.entities.function;

import it.smartcommunitylabdhub.commons.models.entities.task.TaskBaseSpec;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FunctionTaskBaseSpec extends TaskBaseSpec {

    @NotBlank
    String function;

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        FunctionTaskBaseSpec spec = mapper.convertValue(data, FunctionTaskBaseSpec.class);

        this.function = spec.getFunction();
    }

    public static FunctionTaskBaseSpec from(Map<String, Serializable> map) {
        FunctionTaskBaseSpec spec = new FunctionTaskBaseSpec();
        spec.configure(map);

        return spec;
    }
}
