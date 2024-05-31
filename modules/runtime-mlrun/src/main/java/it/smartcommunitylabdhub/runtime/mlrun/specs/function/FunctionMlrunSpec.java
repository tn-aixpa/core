package it.smartcommunitylabdhub.runtime.mlrun.specs.function;

import io.swagger.v3.oas.annotations.media.Schema;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.entities.function.FunctionBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.objects.SourceCode;
import it.smartcommunitylabdhub.runtime.mlrun.MlrunRuntime;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SpecType(runtime = MlrunRuntime.RUNTIME, kind = MlrunRuntime.RUNTIME, entity = EntityName.FUNCTION)
public class FunctionMlrunSpec extends FunctionBaseSpec {

    @NotNull
    @Schema(title = "fields.sourceCode.title", description = "fields.sourceCode.description")
    private SourceCode<MlrunSourceCodeLanguages> source;

    @Schema(title = "fields.container.image.title", description = "fields.container.image.description")
    private String image;

    @Schema(title = "fields.container.tag.title", description = "fields.container.tag.description")
    private String tag;

    @Schema(title = "fields.sourceCode.handler.title", description = "fields.sourceCode.handler.description")
    private String handler;

    @Schema(title = "fields.container.command.title", description = "fields.container.command.description")
    private String command;

    @Schema(title = "fields.python.requirements.title", description = "fields.python.requirements.description")
    private List<Serializable> requirements;

    public FunctionMlrunSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        FunctionMlrunSpec spec = mapper.convertValue(data, FunctionMlrunSpec.class);

        this.source = spec.getSource();
        this.image = spec.getImage();
        this.tag = spec.getTag();
        this.handler = spec.getHandler();
        this.command = spec.getCommand();
        this.requirements = spec.getRequirements();
    }

    public enum MlrunSourceCodeLanguages {
        python,
    }
}
