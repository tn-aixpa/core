package it.smartcommunitylabdhub.commons.models.entities.run;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.commons.models.base.BaseSpec;
import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RunBaseSpec extends BaseSpec {

    @NotEmpty
    private String task;

    @NotEmpty
    @JsonProperty("task_id")
    private String taskId;

    private Map<String, Object> inputs = new HashMap<>();

    private Map<String, Object> outputs = new HashMap<>();

    private Map<String, Object> parameters = new HashMap<>();

    @JsonProperty("local_execution")
    private Boolean localExecution = false;

    @Override
    public void configure(Map<String, Serializable> data) {
        RunBaseSpec runBaseSpec = mapper.convertValue(data, RunBaseSpec.class);

        this.setTask(runBaseSpec.getTask());
        this.setTaskId(runBaseSpec.getTaskId());
        this.setInputs(runBaseSpec.getInputs());
        this.setOutputs(runBaseSpec.getOutputs());
        this.setParameters(runBaseSpec.getParameters());
        this.setLocalExecution(runBaseSpec.getLocalExecution());
    }
}
