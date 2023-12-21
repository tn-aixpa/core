package it.smartcommunitylabdhub.modules.nefertem.models.specs.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.core.annotations.common.SpecType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.models.entities.task.specs.TaskBaseSpec;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@SpecType(kind = "profile", entity = EntityName.TASK)
public class TaskProfileSpec extends TaskBaseSpec<TaskProfileSpec> {

    private String framework;

    @JsonProperty("exec_args")
    private Map<String, Object> execArgs;

    private Boolean parallel;

    @JsonProperty("num_worker")
    private Integer numWorker;

    @Override
    protected void configureSpec(TaskProfileSpec taskProfileSpec) {
        super.configureSpec(taskProfileSpec);

        this.setFramework(taskProfileSpec.getFramework());
        this.setExecArgs(taskProfileSpec.getExecArgs());
        this.setParallel(taskProfileSpec.getParallel());
        this.setNumWorker(taskProfileSpec.getNumWorker());
    }
}
