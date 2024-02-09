package it.smartcommunitylabdhub.runtime.mlrun.models.specs.function;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.entities.function.specs.FunctionBaseSpec;
import it.smartcommunitylabdhub.commons.utils.jackson.JacksonMapper;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SpecType(
  kind = "mlrun",
  runtime = "mlrun",
  entity = EntityName.FUNCTION,
  factory = FunctionMlrunSpec.class
)
public class FunctionMlrunSpec extends FunctionBaseSpec {

  private String image;
  private String tag;
  private String handler;
  private String command;
  private List<Serializable> requirements;

  @Override
  public void configure(Map<String, Object> data) {
    FunctionMlrunSpec functionMlrunSpec =
      JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
        data,
        FunctionMlrunSpec.class
      );

    this.setImage(functionMlrunSpec.getImage());
    this.setTag(functionMlrunSpec.getTag());
    this.setHandler(functionMlrunSpec.getHandler());
    this.setCommand(functionMlrunSpec.getCommand());
    this.setRequirements(functionMlrunSpec.getRequirements());
    super.configure(data);

    this.setExtraSpecs(functionMlrunSpec.getExtraSpecs());
  }
}
