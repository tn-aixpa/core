package it.smartcommunitylabdhub.runtime.huggingface.specs;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.runtime.huggingface.HuggingfaceServeRuntime;
import it.smartcommunitylabdhub.runtime.modelserve.specs.ModelServeFunctionSpec;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SpecType(
    runtime = HuggingfaceServeRuntime.RUNTIME,
    kind = HuggingfaceServeRuntime.RUNTIME,
    entity = EntityName.FUNCTION
)
public class HuggingfaceServeFunctionSpec extends ModelServeFunctionSpec {

    public static HuggingfaceServeFunctionSpec with(Map<String, Serializable> data) {
        HuggingfaceServeFunctionSpec spec = new HuggingfaceServeFunctionSpec();
        spec.configure(data);
        return spec;
    }
}
