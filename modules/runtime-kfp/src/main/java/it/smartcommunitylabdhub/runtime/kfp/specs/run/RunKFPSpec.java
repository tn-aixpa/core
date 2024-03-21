package it.smartcommunitylabdhub.runtime.kfp.specs.run;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.runtime.kfp.KFPRuntime;
import it.smartcommunitylabdhub.runtime.kfp.specs.function.FunctionKFPSpec;
import it.smartcommunitylabdhub.runtime.kfp.specs.task.TaskPipelineSpec;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SpecType(runtime = KFPRuntime.RUNTIME, kind = RunKFPSpec.KIND, entity = EntityName.RUN)
public class RunKFPSpec extends RunBaseSpec {

    public static final String KIND = KFPRuntime.RUNTIME + "+run";

    private List<Map.Entry<String, Serializable>> inputs = new LinkedList<>();

    private List<Map.Entry<String, Serializable>> outputs = new LinkedList<>();

    private Map<String, Serializable> parameters = new HashMap<>();

    @JsonProperty("function_spec")
    private FunctionKFPSpec funcSpec;

    @JsonProperty("pipeline_spec")
    private TaskPipelineSpec taskSpec;


    public RunKFPSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        RunKFPSpec spec = mapper.convertValue(data, RunKFPSpec.class);
        this.inputs = spec.getInputs();
        this.outputs = spec.getOutputs();
        this.parameters = spec.getParameters();

        this.taskSpec = spec.getTaskSpec();
        this.funcSpec = spec.getFuncSpec();
    }
}
