package it.smartcommunitylabdhub.runtime.huggingface.specs;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.jackson.annotations.JsonSchemaIgnore;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.runtime.huggingface.HuggingfaceServeRuntime;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SpecType(runtime = HuggingfaceServeRuntime.RUNTIME, kind = HuggingfaceServeRunSpec.KIND, entity = EntityName.RUN)
public class HuggingfaceServeRunSpec extends RunBaseSpec {

    @JsonSchemaIgnore
    @JsonUnwrapped
    private HuggingfaceServeFunctionSpec functionSpec;

    @JsonUnwrapped
    private HuggingfaceServeTaskSpec taskServeSpec;

    public static final String KIND = HuggingfaceServeRuntime.RUNTIME + "+run";

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        HuggingfaceServeRunSpec spec = mapper.convertValue(data, HuggingfaceServeRunSpec.class);

        this.functionSpec = spec.getFunctionSpec();
        this.taskServeSpec = spec.getTaskServeSpec();
    }

    public void setFunctionSpec(HuggingfaceServeFunctionSpec functionSpec) {
        this.functionSpec = functionSpec;
    }

    public void setTaskServeSpec(HuggingfaceServeTaskSpec taskServeSpec) {
        this.taskServeSpec = taskServeSpec;
    }

    public static HuggingfaceServeRunSpec with(Map<String, Serializable> data) {
        HuggingfaceServeRunSpec spec = new HuggingfaceServeRunSpec();
        spec.configure(data);
        return spec;
    }
}
