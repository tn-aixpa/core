package it.smartcommunitylabdhub.runtime.nefertem.specs.function;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.entities.function.FunctionBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.runtime.nefertem.NefertemRuntime;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SpecType(runtime = NefertemRuntime.RUNTIME, kind = "nefertem", entity = EntityName.FUNCTION)
public class FunctionNefertemSpec extends FunctionBaseSpec {

    private List<Map<String, Object>> constraints;

    private List<Map<String, Object>> metrics;

    @JsonProperty("error_report")
    private String errorReport;

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        FunctionNefertemSpec functionNefertemSpec = mapper.convertValue(data, FunctionNefertemSpec.class);

        this.setConstraints(functionNefertemSpec.getConstraints());
        this.setMetrics(functionNefertemSpec.getMetrics());
        this.setErrorReport(functionNefertemSpec.getErrorReport());
    }
}
