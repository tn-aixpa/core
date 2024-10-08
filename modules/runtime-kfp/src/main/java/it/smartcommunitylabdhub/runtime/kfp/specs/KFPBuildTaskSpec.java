package it.smartcommunitylabdhub.runtime.kfp.specs;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.framework.k8s.base.K8sTaskBaseSpec;
import it.smartcommunitylabdhub.runtime.kfp.KFPRuntime;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SpecType(runtime = KFPRuntime.RUNTIME, kind = KFPBuildTaskSpec.KIND, entity = EntityName.TASK)
public class KFPBuildTaskSpec extends K8sTaskBaseSpec {

    public static final String KIND = "kfp+build";

    public KFPBuildTaskSpec(Map<String, Serializable> data) {
        configure(data);
    }
}
