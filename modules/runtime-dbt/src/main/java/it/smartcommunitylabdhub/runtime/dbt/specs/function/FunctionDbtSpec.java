package it.smartcommunitylabdhub.runtime.dbt.specs.function;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.jackson.JacksonMapper;
import it.smartcommunitylabdhub.commons.models.entities.function.FunctionBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.runtime.dbt.DbtRuntime;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SpecType(runtime = DbtRuntime.RUNTIME, kind = "dbt", entity = EntityName.FUNCTION)
public class FunctionDbtSpec extends FunctionBaseSpec {

    private String sql;

    @Override
    public void configure(Map<String, Object> data) {
        FunctionDbtSpec functionDbtSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(data, FunctionDbtSpec.class);

        this.setSql(functionDbtSpec.getSql());
        super.configure(data);

        this.setExtraSpecs(functionDbtSpec.getExtraSpecs());
    }
}
