package it.smartcommunitylabdhub.core.models.entities.run.specs;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.core.models.base.specs.BaseSpec;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RunBaseSpec<S extends RunBaseSpec<S>> extends BaseSpec<S> {

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
    protected void configureSpec(S concreteSpec) {

        this.setTask(concreteSpec.getTask());
        this.setTaskId(concreteSpec.getTaskId());
        this.setInputs(concreteSpec.getInputs());
        this.setOutputs(concreteSpec.getOutputs());
        this.setParameters(concreteSpec.getParameters());
        this.setLocalExecution(concreteSpec.getLocalExecution());
        this.setExtraSpecs(concreteSpec.getExtraSpecs());

    }
}
