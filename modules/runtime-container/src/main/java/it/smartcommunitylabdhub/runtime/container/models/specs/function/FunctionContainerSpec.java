package it.smartcommunitylabdhub.runtime.container.models.specs.function;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.entities.function.specs.FunctionBaseSpec;
import it.smartcommunitylabdhub.commons.utils.jackson.JacksonMapper;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SpecType(
  kind = "container",
  runtime = "container",
  entity = EntityName.FUNCTION,
  factory = FunctionContainerSpec.class
)
public class FunctionContainerSpec extends FunctionBaseSpec {

  @NotBlank
  private String image;

  @JsonProperty("base_image")
  private String baseImage;

  private String command;
  private String entrypoint;
  private List<String> args;

  @Override
  public void configure(Map<String, Object> data) {
    FunctionContainerSpec functionContainerSpec =
      JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
        data,
        FunctionContainerSpec.class
      );

    this.setCommand(functionContainerSpec.getCommand());
    this.setImage(functionContainerSpec.getImage());
    this.setBaseImage(functionContainerSpec.getBaseImage());
    this.setArgs(functionContainerSpec.getArgs());
    this.setEntrypoint(functionContainerSpec.getEntrypoint());
    super.configure(data);

    this.setExtraSpecs(functionContainerSpec.getExtraSpecs());
  }
}
