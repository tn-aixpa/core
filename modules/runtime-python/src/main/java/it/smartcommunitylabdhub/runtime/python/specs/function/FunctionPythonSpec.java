package it.smartcommunitylabdhub.runtime.python.specs.function;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.entities.function.FunctionBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.objects.SourceCode;
import it.smartcommunitylabdhub.runtime.python.PythonRuntime;
import jakarta.validation.constraints.NotNull;
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

    @NotNull
    @Schema(description = "Source code for the dbt function")
    private SourceCode<PythonSourceCodeLanguages> source;

    @Schema(description = "Container image name")
    private String image;

    @Schema(description = "Container image name to be used for building the final image")
    @JsonProperty("base_image")
    private String baseImage;

    @Schema(description = "Override the arguments passed to command")
    private List<String> requirements;

    public FunctionPythonSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        FunctionPythonSpec spec = mapper.convertValue(data, FunctionPythonSpec.class);

        this.image = spec.getImage();
        this.baseImage = spec.getBaseImage();
        this.requirements = spec.getRequirements();
    }
    public enum PythonSourceCodeLanguages {
        python,
    }
}
