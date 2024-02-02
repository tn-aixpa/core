package it.smartcommunitylabdhub.modules.container.models.specs.task;

import it.smartcommunitylabdhub.core.annotations.common.SpecType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.models.entities.task.specs.K8sTaskBaseSpec;
import it.smartcommunitylabdhub.core.utils.jackson.JacksonMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@SpecType(kind = "serve", entity = EntityName.TASK, factory = TaskServeSpec.class)
public class TaskServeSpec extends K8sTaskBaseSpec {

    @Override
    public void configure(Map<String, Object> data) {

        TaskServeSpec taskDeploySpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
                data, TaskServeSpec.class);

        super.configure(data);
        this.setExtraSpecs(taskDeploySpec.getExtraSpecs());

    }
}
