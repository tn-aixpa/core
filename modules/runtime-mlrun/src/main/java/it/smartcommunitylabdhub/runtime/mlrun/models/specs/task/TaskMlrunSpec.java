package it.smartcommunitylabdhub.runtime.mlrun.models.specs.task;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.commons.utils.jackson.JacksonMapper;
import it.smartcommunitylabdhub.framework.k8s.base.K8sTaskBaseSpec;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SpecType(kind = "mlrun+mlrun", entity = EntityName.TASK, factory = TaskMlrunSpec.class)
public class TaskMlrunSpec extends K8sTaskBaseSpec {

    @Override
    public void configure(Map<String, Object> data) {
        TaskMlrunSpec taskMlrunSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(data, TaskMlrunSpec.class);
        super.configure(data);

        this.setExtraSpecs(taskMlrunSpec.getExtraSpecs());
    }
}
