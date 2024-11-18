package it.smartcommunitylabdhub.runtime.container.specs;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.entities.EntityName;
import it.smartcommunitylabdhub.framework.k8s.objects.CorePort;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreServiceType;
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
@SpecType(runtime = ContainerRuntime.RUNTIME, kind = ContainerServeTaskSpec.KIND, entity = EntityName.TASK)
public class ContainerServeTaskSpec extends ContainerDeployTaskSpec {

    public static final String KIND = "container+serve";

    // Port lists
    @JsonProperty("service_ports")
    private List<CorePort> servicePorts;

    // ClusterIP or NodePort
    @JsonProperty("service_type")
    private CoreServiceType serviceType;

    public ContainerServeTaskSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        ContainerServeTaskSpec spec = mapper.convertValue(data, ContainerServeTaskSpec.class);

        this.setServicePorts(spec.getServicePorts());
        this.setServiceType(spec.getServiceType());
    }
}
