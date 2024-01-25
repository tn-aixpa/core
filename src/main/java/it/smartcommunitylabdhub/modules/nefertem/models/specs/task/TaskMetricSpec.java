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
@SpecType(kind = "metric", entity = EntityName.TASK, factory = TaskMetricSpec.class)
public class TaskMetricSpec extends K8sTaskBaseSpec {

    private String framework;

    @JsonProperty("exec_args")
    private Map<String, Object> execArgs;

    private Boolean parallel;

    @JsonProperty("num_worker")
    private Integer numWorker;

    @Override
    public void configure(Map<String, Object> data) {

        TaskMetricSpec taskMetricSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
                data, TaskMetricSpec.class);


        this.setFramework(taskMetricSpec.getFramework());
        this.setExecArgs(taskMetricSpec.getExecArgs());
        this.setParallel(taskMetricSpec.getParallel());
        this.setNumWorker(taskMetricSpec.getNumWorker());

        super.configure(data);
        this.setExtraSpecs(taskMetricSpec.getExtraSpecs());


    }
}
