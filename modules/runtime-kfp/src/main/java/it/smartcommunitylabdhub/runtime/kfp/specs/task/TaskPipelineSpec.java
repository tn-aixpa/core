package it.smartcommunitylabdhub.runtime.kfp.specs.task;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.framework.k8s.base.K8sTaskBaseSpec;
import it.smartcommunitylabdhub.runtime.kfp.KFPRuntime;
import java.io.Serializable;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SpecType(runtime = KFPRuntime.RUNTIME, kind = TaskPipelineSpec.KIND, entity = EntityName.TASK)
public class TaskPipelineSpec extends K8sTaskBaseSpec {

    public static final String KIND = "kfp+pipeline";

    private String schedule;

    public TaskPipelineSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        TaskPipelineSpec spec = mapper.convertValue(data, TaskPipelineSpec.class);
        this.schedule = spec.getSchedule();
    }
}
