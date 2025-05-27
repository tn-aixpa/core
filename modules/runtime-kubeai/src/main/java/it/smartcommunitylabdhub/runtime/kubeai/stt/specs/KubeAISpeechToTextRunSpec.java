package it.smartcommunitylabdhub.runtime.kubeai.stt.specs;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.jackson.annotations.JsonSchemaIgnore;
import it.smartcommunitylabdhub.commons.models.entities.EntityName;
import it.smartcommunitylabdhub.runtime.kubeai.base.KubeAIServeRunSpec;
import it.smartcommunitylabdhub.runtime.kubeai.stt.KubeAISpeechToTextRuntime;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SpecType(runtime = KubeAISpeechToTextRuntime.RUNTIME, kind = KubeAISpeechToTextRunSpec.KIND, entity = EntityName.RUN)
public class KubeAISpeechToTextRunSpec extends KubeAIServeRunSpec {

    public static final String KIND = KubeAISpeechToTextRuntime.RUNTIME + "+run";

    @JsonSchemaIgnore
    @JsonUnwrapped
    private KubeAISpeechToTextFunctionSpec functionSpec;

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        KubeAISpeechToTextRunSpec spec = mapper.convertValue(data, KubeAISpeechToTextRunSpec.class);
        this.functionSpec = spec.getFunctionSpec();
    }

    public void setFunctionSpec(KubeAISpeechToTextFunctionSpec functionSpec) {
        this.functionSpec = functionSpec;
    }

    public static KubeAISpeechToTextRunSpec with(Map<String, Serializable> data) {
        KubeAISpeechToTextRunSpec spec = new KubeAISpeechToTextRunSpec();
        spec.configure(data);
        return spec;
    }
}
