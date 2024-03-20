package it.smartcommunitylabdhub.runtime.mlrun.specs.task;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.entities.task.TaskBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.framework.k8s.base.K8sTaskSpec;
import it.smartcommunitylabdhub.runtime.mlrun.MlrunRuntime;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SpecType(runtime = MlrunRuntime.RUNTIME, kind = TaskMlrunJobSpec.KIND, entity = EntityName.TASK)
public class TaskMlrunJobSpec extends TaskBaseSpec {

    public static final String KIND = "mlrun+job";

    private K8sTaskSpec k8s = new K8sTaskSpec();

    public TaskMlrunJobSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        TaskMlrunJobSpec spec = mapper.convertValue(data, TaskMlrunJobSpec.class);
        this.k8s = spec.getK8s();
    }
}
