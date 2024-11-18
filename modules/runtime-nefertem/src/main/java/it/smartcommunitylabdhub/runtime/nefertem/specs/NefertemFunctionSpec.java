package it.smartcommunitylabdhub.runtime.nefertem.specs;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.entities.EntityName;
import it.smartcommunitylabdhub.commons.models.function.FunctionBaseSpec;
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
@SpecType(runtime = NefertemRuntime.RUNTIME, kind = NefertemRuntime.RUNTIME, entity = EntityName.FUNCTION)
public class NefertemFunctionSpec extends FunctionBaseSpec {

    private List<Map<String, Serializable>> constraints;
    private List<Map<String, Serializable>> metrics;

    @JsonProperty("error_report")
    private String errorReport;

    public NefertemFunctionSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        NefertemFunctionSpec spec = mapper.convertValue(data, NefertemFunctionSpec.class);

        this.constraints = spec.getConstraints();
        this.metrics = spec.getMetrics();
        this.errorReport = spec.getErrorReport();
    }
}
