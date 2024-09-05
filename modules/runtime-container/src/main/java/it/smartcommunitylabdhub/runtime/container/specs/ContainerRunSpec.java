package it.smartcommunitylabdhub.runtime.container.specs;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.jackson.annotations.JsonSchemaIgnore;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.runtime.container.ContainerRuntime;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
// @Setter
@NoArgsConstructor
@SpecType(runtime = ContainerRuntime.RUNTIME, kind = ContainerRunSpec.KIND, entity = EntityName.RUN)
public class ContainerRunSpec extends RunBaseSpec {

    public static final String KIND = ContainerRuntime.RUNTIME + "+run";

    // @JsonProperty("job_spec")
    @JsonUnwrapped
    private ContainerJobTaskSpec taskJobSpec;

    // @JsonProperty("deploy_spec")
    @JsonUnwrapped
    private ContainerDeployTaskSpec taskDeploySpec;

    // @JsonProperty("serve_spec")
    @JsonUnwrapped
    private ContainerServeTaskSpec taskServeSpec;

    // @JsonProperty("build_spec")
    @JsonUnwrapped
    private ContainerBuildTaskSpec taskBuildSpec;

    // @JsonProperty("function_spec")
    @JsonSchemaIgnore
    @JsonUnwrapped
    private ContainerFunctionSpec functionSpec;

    public ContainerRunSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        ContainerRunSpec spec = mapper.convertValue(data, ContainerRunSpec.class);

        this.taskJobSpec = spec.getTaskJobSpec();
        this.taskDeploySpec = spec.getTaskDeploySpec();
        this.taskServeSpec = spec.getTaskServeSpec();
        this.functionSpec = spec.getFunctionSpec();
        this.taskBuildSpec = spec.getTaskBuildSpec();
    }

    public void setFunctionSpec(ContainerFunctionSpec functionSpec) {
        this.functionSpec = functionSpec;
    }

    public void setTaskJobSpec(ContainerJobTaskSpec taskJobSpec) {
        this.taskJobSpec = taskJobSpec;
    }

    public void setTaskDeploySpec(ContainerDeployTaskSpec taskDeploySpec) {
        this.taskDeploySpec = taskDeploySpec;
    }

    public void setTaskServeSpec(ContainerServeTaskSpec taskServeSpec) {
        this.taskServeSpec = taskServeSpec;
    }

    public void setTaskBuildSpec(ContainerBuildTaskSpec taskBuildSpec) {
        this.taskBuildSpec = taskBuildSpec;
    }
}
