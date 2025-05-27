package it.smartcommunitylabdhub.runtime.kubeai.stt.specs;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.entities.EntityName;
import it.smartcommunitylabdhub.runtime.kubeai.base.KubeAIServeFunctionSpec;
import it.smartcommunitylabdhub.runtime.kubeai.stt.KubeAISpeechToTextRuntime;
import java.io.Serializable;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@SpecType(
    runtime = KubeAISpeechToTextRuntime.RUNTIME,
    kind = KubeAISpeechToTextRuntime.RUNTIME,
    entity = EntityName.FUNCTION
)
public class KubeAISpeechToTextFunctionSpec extends KubeAIServeFunctionSpec {

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);
    }

    public static KubeAISpeechToTextFunctionSpec with(Map<String, Serializable> data) {
        KubeAISpeechToTextFunctionSpec spec = new KubeAISpeechToTextFunctionSpec();
        spec.configure(data);
        return spec;
    }
}
