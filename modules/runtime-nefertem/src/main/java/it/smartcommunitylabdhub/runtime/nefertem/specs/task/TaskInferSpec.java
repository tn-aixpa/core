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
@SpecType(runtime = NefertemRuntime.RUNTIME, kind = TaskInferSpec.KIND, entity = EntityName.TASK)
public class TaskInferSpec extends K8sTaskBaseSpec {

    public static final String KIND = "nefertem+infer";

    private String framework;

    @JsonProperty("exec_args")
    private Map<String, Serializable> execArgs;

    private Boolean parallel;

    @JsonProperty("num_worker")
    private Integer numWorker;

    public TaskInferSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        TaskInferSpec spec = mapper.convertValue(data, TaskInferSpec.class);

        this.framework = spec.getFramework();
        this.execArgs = spec.getExecArgs();
        this.parallel = spec.getParallel();
        this.numWorker = spec.getNumWorker();
    }
}
