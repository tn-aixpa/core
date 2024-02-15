package it.smartcommunitylabdhub.runtime.nefertem.specs.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.framework.k8s.base.K8sTaskBaseSpec;
import it.smartcommunitylabdhub.runtime.nefertem.NefertemRuntime;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SpecType(runtime = NefertemRuntime.RUNTIME, kind = TaskValidateSpec.KIND, entity = EntityName.TASK)
public class TaskValidateSpec extends K8sTaskBaseSpec {

    public static final String KIND = "nefertem+validate";

    private String framework;

    @JsonProperty("exec_args")
    private Map<String, Object> execArgs;

    private Boolean parallel;

    @JsonProperty("num_worker")
    private Integer numWorker;

    public TaskValidateSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        TaskValidateSpec taskValidateSpec = mapper.convertValue(data, TaskValidateSpec.class);

        this.setFramework(taskValidateSpec.getFramework());
        this.setExecArgs(taskValidateSpec.getExecArgs());
        this.setParallel(taskValidateSpec.getParallel());
        this.setNumWorker(taskValidateSpec.getNumWorker());
    }
}
