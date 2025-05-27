package it.smartcommunitylabdhub.runtime.kubeai.stt.specs;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.entities.EntityName;
import it.smartcommunitylabdhub.commons.models.function.FunctionTaskBaseSpec;
import it.smartcommunitylabdhub.runtime.kubeai.stt.KubeAISpeechToTextRuntime;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SpecType(
    runtime = KubeAISpeechToTextRuntime.RUNTIME,
    kind = KubeAISpeechToTextServeTaskSpec.KIND,
    entity = EntityName.TASK
)
public class KubeAISpeechToTextServeTaskSpec extends FunctionTaskBaseSpec {

    public static final String KIND = KubeAISpeechToTextRuntime.RUNTIME + "+serve";

    public static KubeAISpeechToTextServeTaskSpec with(Map<String, Serializable> data) {
        KubeAISpeechToTextServeTaskSpec spec = new KubeAISpeechToTextServeTaskSpec();
        spec.configure(data);
        return spec;
    }
}
