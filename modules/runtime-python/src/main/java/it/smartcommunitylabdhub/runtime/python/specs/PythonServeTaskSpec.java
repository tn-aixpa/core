package it.smartcommunitylabdhub.runtime.python.specs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.entities.EntityName;
import it.smartcommunitylabdhub.framework.k8s.base.K8sFunctionTaskBaseSpec;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreServiceType;
import it.smartcommunitylabdhub.runtime.python.PythonRuntime;
import jakarta.validation.constraints.Min;
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
public class PythonServeTaskSpec extends K8sFunctionTaskBaseSpec {

    public static final String KIND = "python+serve";

    @JsonProperty("replicas")
    @Min(0)
    private Integer replicas;

    @JsonProperty(value = "service_type", defaultValue = "ClusterIP")
    @Schema(defaultValue = "ClusterIP")
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
