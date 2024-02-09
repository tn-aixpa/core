package it.smartcommunitylabdhub.runtime.container.models.specs.run;


import com.fasterxml.jackson.annotation.JsonProperty;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.utils.jackson.JacksonMapper;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.models.entities.run.specs.RunBaseSpec;
import it.smartcommunitylabdhub.runtime.container.models.specs.function.FunctionContainerSpec;
import it.smartcommunitylabdhub.runtime.container.models.specs.task.TaskDeploySpec;
import it.smartcommunitylabdhub.runtime.container.models.specs.task.TaskJobSpec;
import it.smartcommunitylabdhub.runtime.container.models.specs.task.TaskServeSpec;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SpecType(kind = "run", runtime = "container", entity = EntityName.RUN, factory = RunContainerSpec.class)
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

        RunContainerSpec runContainerSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(data, RunContainerSpec.class);

        this.setTaskJobSpec(runContainerSpec.getTaskJobSpec());
        this.setTaskDeploySpec(runContainerSpec.getTaskDeploySpec());
        this.setTaskServeSpec(runContainerSpec.getTaskServeSpec());
        this.setFuncSpec(runContainerSpec.getFuncSpec());

        super.configure(data);
        this.setExtraSpecs(runContainerSpec.getExtraSpecs());
    }
}