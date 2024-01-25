package it.smartcommunitylabdhub.modules.nefertem.models.specs.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.core.annotations.common.SpecType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.models.entities.task.specs.K8sTaskBaseSpec;
import it.smartcommunitylabdhub.core.utils.jackson.JacksonMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@SpecType(kind = "infer", entity = EntityName.TASK, factory = TaskInferSpec.class)
public class TaskInferSpec extends K8sTaskBaseSpec {

    private String framework;

    @JsonProperty("exec_args")
    private Map<String, Object> execArgs;

    private Boolean parallel;

    @JsonProperty("num_worker")
    private Integer numWorker;


    @Override
    public void configure(Map<String, Object> data) {

        TaskInferSpec taskInferSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
                data, TaskInferSpec.class);

        this.setFramework(taskInferSpec.getFramework());
        this.setExecArgs(taskInferSpec.getExecArgs());
        this.setParallel(taskInferSpec.getParallel());
        this.setNumWorker(taskInferSpec.getNumWorker());

        super.configure(data);

        this.setExtraSpecs(taskInferSpec.getExtraSpecs());
    }
}
