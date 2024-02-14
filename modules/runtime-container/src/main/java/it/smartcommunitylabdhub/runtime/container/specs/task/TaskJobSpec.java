package it.smartcommunitylabdhub.runtime.container.specs.task;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.jackson.JacksonMapper;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.framework.k8s.base.K8sTaskBaseSpec;
import it.smartcommunitylabdhub.runtime.container.ContainerRuntime;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SpecType(runtime = ContainerRuntime.RUNTIME, kind = "container+job", entity = EntityName.TASK)
public class TaskJobSpec extends K8sTaskBaseSpec {

    @Override
    public void configure(Map<String, Object> data) {
        TaskJobSpec taskDeploySpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(data, TaskJobSpec.class);

        super.configure(data);
        this.setExtraSpecs(taskDeploySpec.getExtraSpecs());
    }
}
