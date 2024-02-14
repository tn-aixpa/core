package it.smartcommunitylabdhub.runtime.nefertem.specs.function;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.jackson.JacksonMapper;
import it.smartcommunitylabdhub.commons.models.entities.function.FunctionBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SpecType(kind = "nefertem", entity = EntityName.FUNCTION)
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
