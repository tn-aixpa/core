package it.smartcommunitylabdhub.runtime.nefertem.models.specs.task;

import com.fasterxml.jackson.annotation.JsonProperty;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.utils.jackson.JacksonMapper;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.framework.k8s.base.K8sTaskBaseSpec;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@SpecType(kind = "infer", runtime = "nefertem", entity = EntityName.TASK, factory = TaskInferSpec.class)
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
