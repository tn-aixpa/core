package it.smartcommunitylabdhub.runtime.dbt.specs.function;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.entities.function.FunctionBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.objects.SourceCode;
import it.smartcommunitylabdhub.runtime.dbt.DbtRuntime;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SpecType(runtime = DbtRuntime.RUNTIME, kind = "dbt", entity = EntityName.FUNCTION)
public class FunctionDbtSpec extends FunctionBaseSpec {

    private SourceCode sql;

    public FunctionDbtSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        FunctionDbtSpec functionDbtSpec = mapper.convertValue(data, FunctionDbtSpec.class);

        this.setSql(functionDbtSpec.getSql());
    }
}
