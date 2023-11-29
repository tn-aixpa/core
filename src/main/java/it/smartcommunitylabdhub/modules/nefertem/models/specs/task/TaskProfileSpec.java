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

    @JsonProperty("run_config")
    private Map<String, Object> runConfig;

    @Override
    protected void configureSpec(TaskProfileSpec taskProfileSpec) {
        super.configureSpec(taskProfileSpec);

        this.setRunConfig(taskProfileSpec.getRunConfig());
    }
}
