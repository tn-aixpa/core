package it.smartcommunitylabdhub.runtime.mlrun.specs.task;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.jackson.JacksonMapper;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.framework.k8s.base.K8sTaskBaseSpec;
import it.smartcommunitylabdhub.runtime.mlrun.MlrunRuntime;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SpecType(runtime = MlrunRuntime.RUNTIME, kind = "mlrun+mlrun", entity = EntityName.TASK)
public class TaskMlrunSpec extends K8sTaskBaseSpec {

    @Override
    public void configure(Map<String, Object> data) {
        TaskMlrunSpec taskMlrunSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(data, TaskMlrunSpec.class);
        super.configure(data);

        this.setExtraSpecs(taskMlrunSpec.getExtraSpecs());
    }
}
