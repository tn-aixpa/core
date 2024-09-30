package it.smartcommunitylabdhub.runtime.modelserve.specs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import it.smartcommunitylabdhub.framework.k8s.base.K8sTaskBaseSpec;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreServiceType;
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
public class ModelServeServeTaskSpec extends K8sTaskBaseSpec {

    @JsonProperty("replicas")
    @Min(0)
    private Integer replicas;

    @JsonProperty(value = "scale_to_zero", defaultValue = "False")
    @Schema(defaultValue = "False")
    private Boolean scaleToZero;

    @JsonProperty("inactivity_period")
    private Long inactivityPeriod;

    // ClusterIP or NodePort
    @JsonProperty(value = "service_type", defaultValue = "NodePort")
    @Schema(defaultValue = "NodePort")
    private CoreServiceType serviceType;

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        ModelServeServeTaskSpec spec = mapper.convertValue(data, ModelServeServeTaskSpec.class);

        this.replicas = spec.getReplicas();
        this.scaleToZero = spec.getScaleToZero();
        this.inactivityPeriod = spec.getInactivityPeriod();

        this.setServiceType(spec.getServiceType());
    }
}
