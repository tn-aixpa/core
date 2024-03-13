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
@SpecType(runtime = ContainerRuntime.RUNTIME, kind = ContainerRuntime.RUNTIME, entity = EntityName.FUNCTION)
public class FunctionContainerSpec extends FunctionBaseSpec {

    @NotBlank
    @Schema(description = "Container image name")
    private String image;

    @Schema(description = "Container image name to be used for building the final image")
    @JsonProperty("base_image")
    private String baseImage;

    @Schema(description = "Override the command run in the container")
    private String command;

    @Schema(description = "Override the arguments passed to command")
    private List<String> args;

    public FunctionContainerSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        FunctionContainerSpec spec = mapper.convertValue(data, FunctionContainerSpec.class);

        this.command = spec.getCommand();
        this.image = spec.getImage();
        this.baseImage = spec.getBaseImage();
        this.args = spec.getArgs();
    }
}
