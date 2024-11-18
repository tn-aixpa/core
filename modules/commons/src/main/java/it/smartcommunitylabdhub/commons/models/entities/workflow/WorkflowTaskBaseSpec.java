package it.smartcommunitylabdhub.commons.models.entities.workflow;

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
public class WorkflowTaskBaseSpec extends TaskBaseSpec {

    @NotBlank
    String workflow;

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        WorkflowTaskBaseSpec spec = mapper.convertValue(data, WorkflowTaskBaseSpec.class);

        this.workflow = spec.getWorkflow();
    }

    public static WorkflowTaskBaseSpec from(Map<String, Serializable> map) {
        WorkflowTaskBaseSpec spec = new WorkflowTaskBaseSpec();
        spec.configure(map);

        return spec;
    }
}
