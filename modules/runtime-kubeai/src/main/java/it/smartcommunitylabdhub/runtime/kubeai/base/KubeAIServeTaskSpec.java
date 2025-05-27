package it.smartcommunitylabdhub.runtime.kubeai.base;

import it.smartcommunitylabdhub.commons.models.function.FunctionTaskBaseSpec;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class KubeAIServeTaskSpec extends FunctionTaskBaseSpec {

    public static KubeAIServeTaskSpec with(Map<String, Serializable> data) {
        KubeAIServeTaskSpec spec = new KubeAIServeTaskSpec();
        spec.configure(data);
        return spec;
    }
}
