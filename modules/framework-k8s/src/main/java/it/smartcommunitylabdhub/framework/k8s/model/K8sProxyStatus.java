package it.smartcommunitylabdhub.framework.k8s.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseStatus;
import java.io.Serializable;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class K8sProxyStatus extends RunBaseStatus {

    private Map<String, Serializable> proxy;

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        K8sProxyStatus spec = mapper.convertValue(data, K8sProxyStatus.class);
        this.proxy = spec.getProxy();
    }
}
