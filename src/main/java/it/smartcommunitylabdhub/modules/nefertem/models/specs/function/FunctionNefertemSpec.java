package it.smartcommunitylabdhub.modules.nefertem.models.specs.function;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.core.annotations.common.SpecType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.models.entities.function.specs.FunctionBaseSpec;
import it.smartcommunitylabdhub.core.utils.jackson.JacksonMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;


@Getter
@Setter
@SpecType(kind = "nefertem", entity = EntityName.FUNCTION, factory = FunctionNefertemSpec.class)
public class FunctionNefertemSpec extends FunctionBaseSpec {

    private List<Map<String, Object>> constraints;

    private List<Map<String, Object>> metrics;

    @JsonProperty("error_report")
    private String errorReport;

    @Override
    public void configure(Map<String, Object> data) {

        FunctionNefertemSpec functionNefertemSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
                data, FunctionNefertemSpec.class);

        this.setConstraints(functionNefertemSpec.getConstraints());
        this.setMetrics(functionNefertemSpec.getMetrics());
        this.setErrorReport(functionNefertemSpec.getErrorReport());

        super.configure(data);
        this.setExtraSpecs(functionNefertemSpec.getExtraSpecs());
    }
}
