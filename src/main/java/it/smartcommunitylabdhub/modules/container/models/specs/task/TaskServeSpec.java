package it.smartcommunitylabdhub.modules.container.models.specs.task;

import it.smartcommunitylabdhub.core.annotations.common.SpecType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.utils.jackson.JacksonMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@SpecType(kind = "serve", entity = EntityName.TASK, factory = TaskServeSpec.class)
public class TaskServeSpec extends TaskDeploySpec {

    /// TODO: Service parameters port list...ClusterIP or NodePort

    @Override
    public void configure(Map<String, Object> data) {

        TaskServeSpec taskDeploySpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
                data, TaskServeSpec.class);

        super.configure(data);
        this.setExtraSpecs(taskDeploySpec.getExtraSpecs());

    }
}
