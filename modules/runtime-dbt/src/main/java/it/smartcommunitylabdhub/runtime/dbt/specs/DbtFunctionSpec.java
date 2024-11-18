package it.smartcommunitylabdhub.runtime.dbt.specs;

import io.swagger.v3.oas.annotations.media.Schema;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.entities.EntityName;
import it.smartcommunitylabdhub.commons.models.function.FunctionBaseSpec;
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
public class DbtFunctionSpec extends FunctionBaseSpec {

    @NotNull
    @Schema(title = "fields.sourceCode.title", description = "fields.sourceCode.description")
    private SourceCode<DbtSourceCodeLanguages> source;

    public DbtFunctionSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        DbtFunctionSpec spec = mapper.convertValue(data, DbtFunctionSpec.class);
        this.source = spec.getSource();
    }

    public enum DbtSourceCodeLanguages {
        sql,
        python,
    }
}
