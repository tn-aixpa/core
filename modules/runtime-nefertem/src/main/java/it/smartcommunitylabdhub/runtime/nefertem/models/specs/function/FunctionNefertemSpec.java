package it.smartcommunitylabdhub.runtime.nefertem.models.specs.function;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.entities.function.specs.FunctionBaseSpec;
import it.smartcommunitylabdhub.commons.utils.jackson.JacksonMapper;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

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
            data,
            FunctionNefertemSpec.class
        );

        this.setConstraints(functionNefertemSpec.getConstraints());
        this.setMetrics(functionNefertemSpec.getMetrics());
        this.setErrorReport(functionNefertemSpec.getErrorReport());

        super.configure(data);
        this.setExtraSpecs(functionNefertemSpec.getExtraSpecs());
    }
}
