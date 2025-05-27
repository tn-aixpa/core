package it.smartcommunitylabdhub.runtime.kubeai.text.specs;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.entities.EntityName;
import it.smartcommunitylabdhub.commons.models.function.FunctionTaskBaseSpec;
import it.smartcommunitylabdhub.runtime.kubeai.text.KubeAITextRuntime;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SpecType(runtime = KubeAITextRuntime.RUNTIME, kind = KubeAITextServeTaskSpec.KIND, entity = EntityName.TASK)
public class KubeAITextServeTaskSpec extends FunctionTaskBaseSpec {

    public static final String KIND = KubeAITextRuntime.RUNTIME + "+serve";

    public static KubeAITextServeTaskSpec with(Map<String, Serializable> data) {
        KubeAITextServeTaskSpec spec = new KubeAITextServeTaskSpec();
        spec.configure(data);
        return spec;
    }
}
