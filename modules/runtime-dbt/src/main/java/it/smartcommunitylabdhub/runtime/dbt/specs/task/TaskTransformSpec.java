package it.smartcommunitylabdhub.runtime.dbt.specs.task;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.entities.task.TaskBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.framework.k8s.base.K8sTaskSpec;
import it.smartcommunitylabdhub.runtime.dbt.DbtRuntime;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SpecType(runtime = DbtRuntime.RUNTIME, kind = TaskTransformSpec.KIND, entity = EntityName.TASK)
public class TaskTransformSpec extends TaskBaseSpec {

    public static final String KIND = "dbt+transform";

    private K8sTaskSpec k8s = new K8sTaskSpec();

    public TaskTransformSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        TaskTransformSpec spec = mapper.convertValue(data, TaskTransformSpec.class);
        this.k8s = spec.getK8s();
    }
}
