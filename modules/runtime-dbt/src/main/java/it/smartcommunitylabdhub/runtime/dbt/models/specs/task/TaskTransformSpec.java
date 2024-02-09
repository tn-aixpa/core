package it.smartcommunitylabdhub.runtime.dbt.models.specs.task;

import java.util.Map;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.commons.utils.jackson.JacksonMapper;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SpecType(
  kind = "transform",
  runtime = "dbt",
  entity = EntityName.TASK,
  factory = TaskTransformSpec.class
)
public class TaskTransformSpec extends K8sTaskBaseSpec {

  @Override
  public void configure(Map<String, Object> data) {
    TaskTransformSpec taskTransformSpec =
      JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
        data,
        TaskTransformSpec.class
      );

    super.configure(data);
    this.setExtraSpecs(taskTransformSpec.getExtraSpecs());
  }
}
