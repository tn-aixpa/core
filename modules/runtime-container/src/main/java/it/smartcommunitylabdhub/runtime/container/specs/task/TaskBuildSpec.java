package it.smartcommunitylabdhub.runtime.container.specs.task;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.framework.k8s.base.K8sTaskBaseSpec;
import it.smartcommunitylabdhub.runtime.container.ContainerRuntime;
import it.smartcommunitylabdhub.runtime.container.docker.DockerfileInstruction;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SpecType(runtime = ContainerRuntime.RUNTIME, kind = TaskBuildSpec.KIND, entity = EntityName.TASK)
public class TaskBuildSpec extends K8sTaskBaseSpec {

    public static final String KIND = "container+build";

    private Integer replicas;

    private Map<DockerfileInstruction, List<String>> instructions;

    public TaskBuildSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        TaskBuildSpec spec = mapper.convertValue(data, TaskBuildSpec.class);
        this.replicas = spec.getReplicas();
        this.instructions = spec.getInstructions();
    }
}
