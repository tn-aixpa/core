package it.smartcommunitylabdhub.runtime.kubeai.text.specs;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.jackson.annotations.JsonSchemaIgnore;
import it.smartcommunitylabdhub.commons.models.entities.EntityName;
import it.smartcommunitylabdhub.runtime.kubeai.base.KubeAIServeRunSpec;
import it.smartcommunitylabdhub.runtime.kubeai.text.KubeAITextRuntime;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SpecType(runtime = KubeAITextRuntime.RUNTIME, kind = KubeAITextRunSpec.KIND, entity = EntityName.RUN)
public class KubeAITextRunSpec extends KubeAIServeRunSpec {

    public static final String KIND = KubeAITextRuntime.RUNTIME + "+run";

    @JsonSchemaIgnore
    @JsonUnwrapped
    private KubeAITextFunctionSpec functionSpec;

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        KubeAITextRunSpec spec = mapper.convertValue(data, KubeAITextRunSpec.class);
        this.functionSpec = spec.getFunctionSpec();
    }

    public void setFunctionSpec(KubeAITextFunctionSpec functionSpec) {
        this.functionSpec = functionSpec;
    }

    public static KubeAITextRunSpec with(Map<String, Serializable> data) {
        KubeAITextRunSpec spec = new KubeAITextRunSpec();
        spec.configure(data);
        return spec;
    }
}
