package it.smartcommunitylabdhub.runtime.container.specs.function;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.entities.function.FunctionBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.runtime.container.ContainerRuntime;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SpecType(runtime = ContainerRuntime.RUNTIME, kind = "container", entity = EntityName.FUNCTION)
public class FunctionContainerSpec extends FunctionBaseSpec {

    @NotBlank
    @Schema(defaultValue = "", description = "Container image name")
    private String image;

    @JsonProperty("base_image")
    private String baseImage;

    private String command;
    private List<String> args;

    public FunctionContainerSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        FunctionContainerSpec functionContainerSpec = mapper.convertValue(data, FunctionContainerSpec.class);

        this.setCommand(functionContainerSpec.getCommand());
        this.setImage(functionContainerSpec.getImage());
        this.setBaseImage(functionContainerSpec.getBaseImage());
        this.setArgs(functionContainerSpec.getArgs());
    }
}
