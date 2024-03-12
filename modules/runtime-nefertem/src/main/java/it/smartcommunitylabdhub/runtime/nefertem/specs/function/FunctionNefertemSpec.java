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
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SpecType(runtime = NefertemRuntime.RUNTIME, kind = "nefertem", entity = EntityName.FUNCTION)
public class FunctionNefertemSpec extends FunctionBaseSpec {

    private List<Map<String, Serializable>> constraints;

    private List<Map<String, Serializable>> metrics;

    @JsonProperty("error_report")
    private String errorReport;

    public FunctionNefertemSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        FunctionNefertemSpec functionNefertemSpec = mapper.convertValue(data, FunctionNefertemSpec.class);

        this.setConstraints(functionNefertemSpec.getConstraints());
        this.setMetrics(functionNefertemSpec.getMetrics());
        this.setErrorReport(functionNefertemSpec.getErrorReport());
    }
}
