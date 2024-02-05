package it.smartcommunitylabdhub.modules.container.models.specs.function;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.core.annotations.common.SpecType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.models.entities.function.specs.FunctionBaseSpec;
import it.smartcommunitylabdhub.core.utils.jackson.JacksonMapper;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@SpecType(kind = "container", entity = EntityName.FUNCTION, factory = FunctionContainerSpec.class)
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


        FunctionContainerSpec functionContainerSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
                data, FunctionContainerSpec.class);

        this.setCommand(functionContainerSpec.getCommand());
        this.setImage(functionContainerSpec.getImage());
        this.setBaseImage(functionContainerSpec.getBaseImage());
        this.setArgs(functionContainerSpec.getArgs());
        this.setEntrypoint(functionContainerSpec.getEntrypoint());
        super.configure(data);

        this.setExtraSpecs(functionContainerSpec.getExtraSpecs());
    }
}
