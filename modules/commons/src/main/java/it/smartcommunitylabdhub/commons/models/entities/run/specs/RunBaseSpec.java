package it.smartcommunitylabdhub.commons.models.entities.run.specs;

import com.fasterxml.jackson.annotation.JsonProperty;

import it.smartcommunitylabdhub.commons.models.base.specs.BaseSpec;
import it.smartcommunitylabdhub.commons.utils.jackson.JacksonMapper;
import jakarta.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
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
  public void configure(Map<String, Object> data) {
    RunBaseSpec runBaseSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
      data,
      RunBaseSpec.class
    );

    this.setTask(runBaseSpec.getTask());
    this.setTaskId(runBaseSpec.getTaskId());
    this.setInputs(runBaseSpec.getInputs());
    this.setOutputs(runBaseSpec.getOutputs());
    this.setParameters(runBaseSpec.getParameters());
    this.setLocalExecution(runBaseSpec.getLocalExecution());

    super.configure(data);
    this.setExtraSpecs(runBaseSpec.getExtraSpecs());
  }
}
