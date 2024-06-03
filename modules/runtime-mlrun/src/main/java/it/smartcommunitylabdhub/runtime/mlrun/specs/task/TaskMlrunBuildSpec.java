package it.smartcommunitylabdhub.runtime.mlrun.specs.task;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.entities.task.TaskBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.framework.k8s.base.K8sTaskBaseSpec;
import it.smartcommunitylabdhub.framework.k8s.base.K8sTaskSpec;
import it.smartcommunitylabdhub.runtime.mlrun.MlrunRuntime;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SpecType(runtime = MlrunRuntime.RUNTIME, kind = TaskMlrunBuildSpec.KIND, entity = EntityName.TASK)
public class TaskMlrunBuildSpec extends K8sTaskBaseSpec {

    public static final String KIND = "mlrun+build";

    private List<String> commands;
    @JsonProperty("force_build")
    private Boolean forceBuild;
    @JsonProperty("target_image")
    private String targetImage;

    public TaskMlrunBuildSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        TaskMlrunBuildSpec spec = mapper.convertValue(data, TaskMlrunBuildSpec.class);

        this.commands = spec.getCommands();
        this.forceBuild = spec.getForceBuild();
        this.targetImage = spec.getTargetImage();
    }
}
