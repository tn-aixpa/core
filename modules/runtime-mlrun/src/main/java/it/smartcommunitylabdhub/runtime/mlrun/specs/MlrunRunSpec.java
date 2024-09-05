package it.smartcommunitylabdhub.runtime.mlrun.specs;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.jackson.annotations.JsonSchemaIgnore;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.runtime.mlrun.MlrunRuntime;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SpecType(runtime = MlrunRuntime.RUNTIME, kind = MlrunRunSpec.KIND, entity = EntityName.RUN)
public class MlrunRunSpec extends RunBaseSpec {

    public static final String KIND = MlrunRuntime.RUNTIME + "+run";

    private Map<String, String> inputs = new HashMap<>();

    private Map<String, String> outputs = new HashMap<>();

    private Map<String, Serializable> parameters = new HashMap<>();

    // @JsonProperty("job_spec")
    @JsonUnwrapped
    private MlrunJobTaskSpec jobSpec;

    // @JsonProperty("build_spec")
    @JsonUnwrapped
    private MlrunBuildTaskSpec buildSpec;

    // @JsonProperty("function_spec")
    @JsonSchemaIgnore
    @JsonUnwrapped
    private MlrunFunctionSpec functionSpec;

    public MlrunRunSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        MlrunRunSpec spec = mapper.convertValue(data, MlrunRunSpec.class);
        this.inputs = spec.getInputs();
        this.outputs = spec.getOutputs();
        this.parameters = spec.getParameters();

        this.jobSpec = spec.getJobSpec();
        this.buildSpec = spec.getBuildSpec();
        this.functionSpec = spec.getFunctionSpec();
    }

    public void setJobSpec(MlrunJobTaskSpec jobSpec) {
        this.jobSpec = jobSpec;
    }

    public void setBuildSpec(MlrunBuildTaskSpec buildSpec) {
        this.buildSpec = buildSpec;
    }

    public void setFunctionSpec(MlrunFunctionSpec funcSpec) {
        this.functionSpec = funcSpec;
    }
}
