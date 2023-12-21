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
@SpecType(kind = "validate", entity = EntityName.TASK)
public class TaskValidateSpec extends TaskBaseSpec<TaskValidateSpec> {

    private String framework;

    @JsonProperty("exec_args")
    private Map<String, Object> execArgs;

    private Boolean parallel;

    @JsonProperty("num_worker")
    private Integer numWorker;

    @Override
    protected void configureSpec(TaskValidateSpec taskValidateSpec) {
        super.configureSpec(taskValidateSpec);

        this.setFramework(taskValidateSpec.getFramework());
        this.setExecArgs(taskValidateSpec.getExecArgs());
        this.setParallel(taskValidateSpec.getParallel());
        this.setNumWorker(taskValidateSpec.getNumWorker());

    }
}
