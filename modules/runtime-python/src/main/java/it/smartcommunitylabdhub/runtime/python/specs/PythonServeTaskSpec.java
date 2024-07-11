package it.smartcommunitylabdhub.runtime.python.specs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.framework.k8s.base.K8sTaskBaseSpec;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreServiceType;
import it.smartcommunitylabdhub.runtime.python.PythonRuntime;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SpecType(runtime = PythonRuntime.RUNTIME, kind = PythonServeTaskSpec.KIND, entity = EntityName.TASK)
public class PythonServeTaskSpec extends K8sTaskBaseSpec {

    public static final String KIND = "python+serve";

    @JsonProperty("replicas")
    @Min(0)
    private Integer replicas;

    // ClusterIP or NodePort
    @JsonProperty(value = "service_type", defaultValue = "NodePort")
    @Schema(defaultValue = "NodePort")
    private CoreServiceType serviceType;

    public PythonServeTaskSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        PythonServeTaskSpec spec = mapper.convertValue(data, PythonServeTaskSpec.class);

        this.replicas = spec.getReplicas();
        this.setServiceType(spec.getServiceType());
    }
}
