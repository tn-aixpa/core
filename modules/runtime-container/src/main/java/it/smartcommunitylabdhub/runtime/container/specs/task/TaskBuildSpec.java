package it.smartcommunitylabdhub.runtime.container.specs.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.framework.k8s.base.K8sTaskBaseSpec;
import it.smartcommunitylabdhub.framework.kaniko.runnables.ContextRef;
import it.smartcommunitylabdhub.framework.kaniko.runnables.ContextSource;
import it.smartcommunitylabdhub.runtime.container.ContainerRuntime;
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
    @JsonProperty("context_refs")
    List<ContextRef> contextRefs;
    @JsonProperty("context_sources")
    List<ContextSource> contextSources;
    private Integer replicas;
    private List<String> instructions;


    public TaskBuildSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        TaskBuildSpec spec = mapper.convertValue(data, TaskBuildSpec.class);
        this.replicas = spec.getReplicas();
        this.instructions = spec.getInstructions(); //Dockerfile instructions
        this.contextRefs = spec.getContextRefs(); // List of context refs that need to be materialized
        this.contextSources = spec.getContextSources(); // List of context sources that need to be materialized
    }
}
