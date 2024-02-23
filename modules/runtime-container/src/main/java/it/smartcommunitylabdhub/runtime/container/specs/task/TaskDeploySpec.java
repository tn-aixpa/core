package it.smartcommunitylabdhub.runtime.container.specs.task;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.framework.k8s.base.K8sTaskBaseSpec;
import it.smartcommunitylabdhub.runtime.container.ContainerRuntime;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SpecType(runtime = ContainerRuntime.RUNTIME, kind = TaskDeploySpec.KIND, entity = EntityName.TASK)
public class TaskDeploySpec extends K8sTaskBaseSpec {

    public static final String KIND = "container+deploy";

    private Integer replicas;

    public TaskDeploySpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        TaskDeploySpec spec = mapper.convertValue(data, TaskDeploySpec.class);
        this.replicas = spec.getReplicas();
    }
}
