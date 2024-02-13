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
@SpecType(kind = "nefertem+validate", entity = EntityName.TASK, factory = TaskValidateSpec.class)
public class TaskValidateSpec extends K8sTaskBaseSpec {

    private String framework;

    @JsonProperty("exec_args")
    private Map<String, Object> execArgs;

    private Boolean parallel;

    @JsonProperty("num_worker")
    private Integer numWorker;

    @Override
    public void configure(Map<String, Object> data) {
        TaskValidateSpec taskValidateSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
            data,
            TaskValidateSpec.class
        );

        this.setFramework(taskValidateSpec.getFramework());
        this.setExecArgs(taskValidateSpec.getExecArgs());
        this.setParallel(taskValidateSpec.getParallel());
        this.setNumWorker(taskValidateSpec.getNumWorker());

        super.configure(data);
        this.setExtraSpecs(taskValidateSpec.getExtraSpecs());
    }
}
