package it.smartcommunitylabdhub.runtime.container.specs.run;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.jackson.JacksonMapper;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.runtime.container.ContainerRuntime;
import it.smartcommunitylabdhub.runtime.container.specs.function.FunctionContainerSpec;
import it.smartcommunitylabdhub.runtime.container.specs.task.TaskDeploySpec;
import it.smartcommunitylabdhub.runtime.container.specs.task.TaskJobSpec;
import it.smartcommunitylabdhub.runtime.container.specs.task.TaskServeSpec;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SpecType(runtime = ContainerRuntime.RUNTIME, kind = "container+run", entity = EntityName.RUN)
public class RunContainerSpec extends RunBaseSpec {

    @JsonProperty("job_spec")
    private TaskJobSpec taskJobSpec;

    @JsonProperty("deploy_spec")
    private TaskDeploySpec taskDeploySpec;

    @JsonProperty("serve_spec")
    private TaskServeSpec taskServeSpec;

    @JsonProperty("function_spec")
    private FunctionContainerSpec funcSpec;

    @Override
    public void configure(Map<String, Object> data) {
        RunContainerSpec runContainerSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
            data,
            RunContainerSpec.class
        );

        this.setTaskJobSpec(runContainerSpec.getTaskJobSpec());
        this.setTaskDeploySpec(runContainerSpec.getTaskDeploySpec());
        this.setTaskServeSpec(runContainerSpec.getTaskServeSpec());
        this.setFuncSpec(runContainerSpec.getFuncSpec());

        super.configure(data);
        this.setExtraSpecs(runContainerSpec.getExtraSpecs());
    }
}
