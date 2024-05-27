package it.smartcommunitylabdhub.runtime.python.specs.function;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.entities.function.FunctionBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.runtime.python.PythonRuntime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@SpecType(runtime = PythonRuntime.RUNTIME, kind = PythonRuntime.RUNTIME, entity = EntityName.FUNCTION)
public class FunctionPythonSpec extends FunctionBaseSpec {

    @Schema(description = "Container image name")
    private String image;

    @Schema(description = "Container image name to be used for building the final image")
    @JsonProperty("base_image")
    private String baseImage;

    @Schema(description = "Override the command run in the container")
    private String command;

    @Schema(description = "Override the arguments passed to command")
    private List<String> args;

    public FunctionPythonSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        FunctionPythonSpec spec = mapper.convertValue(data, FunctionPythonSpec.class);

        this.command = spec.getCommand();
        this.image = spec.getImage();
        this.baseImage = spec.getBaseImage();
        this.args = spec.getArgs();
    }
}
