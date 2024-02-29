package it.smartcommunitylabdhub.runtime.kfp.specs.run;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.runtime.kfp.KFPRuntime;
import it.smartcommunitylabdhub.runtime.kfp.specs.function.FunctionKFPSpec;
import it.smartcommunitylabdhub.runtime.kfp.specs.task.TaskPipelineSpec;

import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SpecType(runtime = KFPRuntime.RUNTIME, kind = "kfp+run", entity = EntityName.RUN)
public class RunKFPSpec extends RunBaseSpec {

    @JsonProperty("pipeline_spec")
    private TaskPipelineSpec taskPipelineSpec;

    @JsonProperty("function_spec")
    private FunctionKFPSpec functionSpec;

    public RunKFPSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        RunKFPSpec runContainerSpec = mapper.convertValue(data, RunKFPSpec.class);

        this.setTaskPipelineSpec(runContainerSpec.getTaskPipelineSpec());
        this.setFunctionSpec(runContainerSpec.getFunctionSpec());
    }
}
