package it.smartcommunitylabdhub.runtime.nefertem.models.specs.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.commons.utils.jackson.JacksonMapper;
import it.smartcommunitylabdhub.framework.k8s.base.K8sTaskBaseSpec;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SpecType(kind = "nefertem+profile", entity = EntityName.TASK, factory = TaskProfileSpec.class)
public class TaskProfileSpec extends K8sTaskBaseSpec {

    private String framework;

    @JsonProperty("exec_args")
    private Map<String, Object> execArgs;

    private Boolean parallel;

    @JsonProperty("num_worker")
    private Integer numWorker;

    @Override
    public void configure(Map<String, Object> data) {
        TaskProfileSpec taskProfileSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(data, TaskProfileSpec.class);

        this.setFramework(taskProfileSpec.getFramework());
        this.setExecArgs(taskProfileSpec.getExecArgs());
        this.setParallel(taskProfileSpec.getParallel());
        this.setNumWorker(taskProfileSpec.getNumWorker());

        super.configure(data);
        this.setExtraSpecs(taskProfileSpec.getExtraSpecs());
    }
}
