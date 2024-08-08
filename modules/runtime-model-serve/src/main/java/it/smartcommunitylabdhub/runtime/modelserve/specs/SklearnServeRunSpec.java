package it.smartcommunitylabdhub.runtime.modelserve.specs;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.runtime.modelserve.SklearnServeRuntime;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SpecType(runtime = SklearnServeRuntime.RUNTIME, kind = SklearnServeRunSpec.KIND, entity = EntityName.RUN)
public class SklearnServeRunSpec extends ModelServeRunSpec {

    @JsonUnwrapped
    private SklearnServeFunctionSpec functionSpec;

    public static final String KIND = SklearnServeRuntime.RUNTIME + "+run";

    public SklearnServeRunSpec(Map<String, Serializable> data) {
        configure(data);

        SklearnServeRunSpec spec = mapper.convertValue(data, SklearnServeRunSpec.class);

        this.functionSpec = spec.getFunctionSpec();
    }

    public void setFunctionSpec(SklearnServeFunctionSpec functionSpec) {
        this.functionSpec = functionSpec;
    }
}
