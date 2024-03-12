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
@SpecType(runtime = NefertemRuntime.RUNTIME, kind = TaskProfileSpec.KIND, entity = EntityName.TASK)
public class TaskProfileSpec extends K8sTaskBaseSpec {

    public static final String KIND = "nefertem+profile";

    private String framework;

    @JsonProperty("exec_args")
    private Map<String, Serializable> execArgs;

    private Boolean parallel;

    @JsonProperty("num_worker")
    private Integer numWorker;

    public TaskProfileSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        TaskProfileSpec taskProfileSpec = mapper.convertValue(data, TaskProfileSpec.class);

        this.setFramework(taskProfileSpec.getFramework());
        this.setExecArgs(taskProfileSpec.getExecArgs());
        this.setParallel(taskProfileSpec.getParallel());
        this.setNumWorker(taskProfileSpec.getNumWorker());
    }
}
