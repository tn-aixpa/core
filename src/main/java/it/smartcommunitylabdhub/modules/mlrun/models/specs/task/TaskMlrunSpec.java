package it.smartcommunitylabdhub.modules.mlrun.models.specs.task;

import it.smartcommunitylabdhub.core.annotations.common.SpecType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.models.entities.task.specs.K8sTaskBaseSpec;
import it.smartcommunitylabdhub.core.utils.jackson.JacksonMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@SpecType(kind = "mlrun", runtime = "mlrun", entity = EntityName.TASK, factory = TaskMlrunSpec.class)
public class TaskMlrunSpec extends K8sTaskBaseSpec {

    @Override
    public void configure(Map<String, Object> data) {

        TaskMlrunSpec taskMlrunSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
                data, TaskMlrunSpec.class);
        super.configure(data);

        this.setExtraSpecs(taskMlrunSpec.getExtraSpecs());

    }
}
