package it.smartcommunitylabdhub.runtime.container.models.specs.task;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.commons.utils.jackson.JacksonMapper;
import it.smartcommunitylabdhub.framework.k8s.base.K8sTaskBaseSpec;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SpecType(kind = "deploy", runtime = "container", entity = EntityName.TASK, factory = TaskDeploySpec.class)
public class TaskDeploySpec extends K8sTaskBaseSpec {

    @Override
    public void configure(Map<String, Object> data) {
        TaskDeploySpec taskDeploySpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(data, TaskDeploySpec.class);

        super.configure(data);
        this.setExtraSpecs(taskDeploySpec.getExtraSpecs());
    }
}
