package it.smartcommunitylabdhub.runtime.mlrun.specs;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.framework.k8s.base.K8sTaskBaseSpec;
import it.smartcommunitylabdhub.runtime.mlrun.MlrunRuntime;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SpecType(runtime = MlrunRuntime.RUNTIME, kind = MlrunJobTaskSpec.KIND, entity = EntityName.TASK)
public class MlrunJobTaskSpec extends K8sTaskBaseSpec {

    public static final String KIND = "mlrun+job";

    public MlrunJobTaskSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        MlrunJobTaskSpec spec = mapper.convertValue(data, MlrunJobTaskSpec.class);
    }
}
