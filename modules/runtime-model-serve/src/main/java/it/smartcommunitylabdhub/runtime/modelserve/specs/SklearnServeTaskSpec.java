package it.smartcommunitylabdhub.runtime.modelserve.specs;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.runtime.modelserve.SklearnServeRuntime;
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

    public static final String KIND = "sklearnserve+serve";

    public SklearnServeTaskSpec(Map<String, Serializable> data) {
        configure(data);
    }
}
