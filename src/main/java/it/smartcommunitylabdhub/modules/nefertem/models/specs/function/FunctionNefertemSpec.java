package it.smartcommunitylabdhub.modules.nefertem.models.specs.function;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.core.annotations.common.SpecType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.models.entities.function.specs.FunctionBaseSpec;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;


@Getter
@Setter
@SpecType(kind = "nefertem", entity = EntityName.FUNCTION, factory = FunctionNefertemSpec.class)
public class FunctionNefertemSpec extends FunctionBaseSpec<FunctionNefertemSpec> {

    private List<Map<String, Object>> constraints;

    private List<Map<String, Object>> metrics;

    @JsonProperty("error_report")
    private String errorReport;

    @Override
    protected void configureSpec(FunctionNefertemSpec functionNefertemSpec) {

        super.configureSpec(functionNefertemSpec);
        this.setConstraints(functionNefertemSpec.getConstraints());
        this.setMetrics(functionNefertemSpec.getMetrics());
        this.setErrorReport(functionNefertemSpec.getErrorReport());
    }
}
