package it.smartcommunitylabdhub.runtime.sklearn.specs;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.runtime.modelserve.specs.ModelServeServeTaskSpec;
import it.smartcommunitylabdhub.runtime.sklearn.SklearnServeRuntime;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SpecType(runtime = SklearnServeRuntime.RUNTIME, kind = SklearnServeTaskSpec.KIND, entity = EntityName.TASK)
public class SklearnServeTaskSpec extends ModelServeServeTaskSpec {

    public static final String KIND = SklearnServeRuntime.RUNTIME + "+serve";

    public static SklearnServeTaskSpec with(Map<String, Serializable> data) {
        SklearnServeTaskSpec spec = new SklearnServeTaskSpec();
        spec.configure(data);
        return spec;
    }
}
