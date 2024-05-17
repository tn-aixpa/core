package it.smartcommunitylabdhub.framework.k8s.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.kubernetes.client.custom.ContainerMetrics;
import it.smartcommunitylabdhub.commons.models.base.BaseSpec;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class K8sLogStatus extends BaseSpec {

    private String namespace;
    private String pod;
    private String container;

    private List<Serializable> metrics;

    @Override
    public void configure(Map<String, Serializable> data) {
        K8sLogStatus spec = mapper.convertValue(data, K8sLogStatus.class);

        this.namespace = spec.getNamespace();
        this.pod = spec.getPod();
        this.container = spec.getContainer();

        this.metrics = spec.getMetrics();
    }
}
