package it.smartcommunitylabdhub.runtime.dbt.models.specs.function;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.entities.function.specs.FunctionBaseSpec;
import it.smartcommunitylabdhub.commons.utils.jackson.JacksonMapper;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SpecType(kind = "dbt", entity = EntityName.FUNCTION)
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
