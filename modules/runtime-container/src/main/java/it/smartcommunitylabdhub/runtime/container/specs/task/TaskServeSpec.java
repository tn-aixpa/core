package it.smartcommunitylabdhub.runtime.container.specs.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.framework.k8s.objects.CorePort;
import it.smartcommunitylabdhub.runtime.container.ContainerRuntime;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SpecType(runtime = ContainerRuntime.RUNTIME, kind = TaskServeSpec.KIND, entity = EntityName.TASK)
public class TaskServeSpec extends TaskDeploySpec {

    public static final String KIND = "container+serve";

    // Port lists
    @JsonProperty("service_ports")
    private List<CorePort> servicePorts;

    // ClusterIP or NodePort
    @JsonProperty("service_type")
    private String serviceType;

    public TaskServeSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        TaskServeSpec taskServeSpec = mapper.convertValue(data, TaskServeSpec.class);

        this.setServicePorts(taskServeSpec.getServicePorts());
        this.setServiceType(taskServeSpec.getServiceType());

    }
}
