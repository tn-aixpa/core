package it.smartcommunitylabdhub.runtime.kfp.specs;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.framework.k8s.base.K8sWorkflowTaskBaseSpec;
import it.smartcommunitylabdhub.runtime.kfp.KFPRuntime;
import java.io.Serializable;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@SpecType(runtime = KFPRuntime.RUNTIME, kind = KFPBuildTaskSpec.KIND, entity = EntityName.TASK)
public class KFPBuildTaskSpec extends K8sWorkflowTaskBaseSpec {

    public static final String KIND = "kfp+build";

    public KFPBuildTaskSpec(Map<String, Serializable> data) {
        configure(data);
    }
}
