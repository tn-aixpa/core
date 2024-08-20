package it.smartcommunitylabdhub.runtime.sklearn.specs;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.runtime.modelserve.specs.ModelServeFunctionSpec;
import it.smartcommunitylabdhub.runtime.sklearn.SklearnServeRuntime;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SpecType(runtime = SklearnServeRuntime.RUNTIME, kind = SklearnServeRuntime.RUNTIME, entity = EntityName.FUNCTION)
public class SklearnServeFunctionSpec extends ModelServeFunctionSpec {

    public static SklearnServeFunctionSpec with(Map<String, Serializable> data) {
        SklearnServeFunctionSpec spec = new SklearnServeFunctionSpec();
        spec.configure(data);
        return spec;
    }
}
