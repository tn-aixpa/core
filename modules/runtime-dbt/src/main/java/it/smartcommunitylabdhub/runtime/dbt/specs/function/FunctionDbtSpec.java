package it.smartcommunitylabdhub.runtime.dbt.specs.function;

import io.swagger.v3.oas.annotations.media.Schema;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.entities.function.FunctionBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.objects.SourceCode;
import it.smartcommunitylabdhub.runtime.dbt.DbtRuntime;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SpecType(runtime = DbtRuntime.RUNTIME, kind = DbtRuntime.RUNTIME, entity = EntityName.FUNCTION)
public class FunctionDbtSpec extends FunctionBaseSpec {

    @NotNull
    @Schema(title = "fields.sourceCode.title", description = "fields.sourceCode.description")
    private SourceCode<DbtSourceCodeLanguages> source;

    public FunctionDbtSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        FunctionDbtSpec spec = mapper.convertValue(data, FunctionDbtSpec.class);
        this.source = spec.getSource();
    }

    public enum DbtSourceCodeLanguages {
        sql,
        python,
    }
}
