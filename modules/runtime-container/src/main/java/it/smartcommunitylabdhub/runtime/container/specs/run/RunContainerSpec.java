package it.smartcommunitylabdhub.runtime.container.specs.run;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.runtime.container.ContainerRuntime;
import it.smartcommunitylabdhub.runtime.container.specs.function.FunctionContainerSpec;
import it.smartcommunitylabdhub.runtime.container.specs.task.TaskBuildSpec;
import it.smartcommunitylabdhub.runtime.container.specs.task.TaskDeploySpec;
import it.smartcommunitylabdhub.runtime.container.specs.task.TaskJobSpec;
import it.smartcommunitylabdhub.runtime.container.specs.task.TaskServeSpec;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
// @Setter
@NoArgsConstructor
@SpecType(runtime = ContainerRuntime.RUNTIME, kind = RunContainerSpec.KIND, entity = EntityName.RUN)
public class RunContainerSpec extends RunBaseSpec {

    public static final String KIND = ContainerRuntime.RUNTIME + "+run";

    // @JsonProperty("job_spec")
    @JsonUnwrapped
    private TaskJobSpec taskJobSpec;

    // @JsonProperty("deploy_spec")
    @JsonUnwrapped
    private TaskDeploySpec taskDeploySpec;

    // @JsonProperty("serve_spec")
    @JsonUnwrapped
    private TaskServeSpec taskServeSpec;

    // @JsonProperty("build_spec")
    @JsonUnwrapped
    private TaskBuildSpec taskBuildSpec;

    // @JsonProperty("function_spec")
    @JsonUnwrapped
    private FunctionContainerSpec functionSpec;

    public RunContainerSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        RunContainerSpec spec = mapper.convertValue(data, RunContainerSpec.class);

        this.taskJobSpec = spec.getTaskJobSpec();
        this.taskDeploySpec = spec.getTaskDeploySpec();
        this.taskServeSpec = spec.getTaskServeSpec();
        this.functionSpec = spec.getFunctionSpec();
        this.taskBuildSpec = spec.getTaskBuildSpec();
    }

    public void setFunctionSpec(FunctionContainerSpec functionSpec) {
        this.functionSpec = functionSpec;
    }

    public void setTaskJobSpec(TaskJobSpec taskJobSpec) {
        this.taskJobSpec = taskJobSpec;
    }

    public void setTaskDeploySpec(TaskDeploySpec taskDeploySpec) {
        this.taskDeploySpec = taskDeploySpec;
    }

    public void setTaskServeSpec(TaskServeSpec taskServeSpec) {
        this.taskServeSpec = taskServeSpec;
    }

    public void setTaskBuildSpec(TaskBuildSpec taskBuildSpec) {
        this.taskBuildSpec = taskBuildSpec;
    }
}
