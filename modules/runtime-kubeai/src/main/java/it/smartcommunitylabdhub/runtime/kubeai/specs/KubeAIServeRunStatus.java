package it.smartcommunitylabdhub.runtime.kubeai.specs;

import com.fasterxml.jackson.annotation.JsonInclude;
import it.smartcommunitylabdhub.commons.models.run.RunBaseStatus;
import it.smartcommunitylabdhub.framework.k8s.model.K8sServiceInfo;
import it.smartcommunitylabdhub.runtime.kubeai.models.OpenAIService;
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
public class KubeAIServeRunStatus extends RunBaseStatus {

    private OpenAIService openai;
    private K8sServiceInfo service;

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        KubeAIServeRunStatus spec = mapper.convertValue(data, KubeAIServeRunStatus.class);
        this.openai = spec.getOpenai();
        this.service = spec.getService();
    }

    public static KubeAIServeRunStatus with(Map<String, Serializable> data) {
        KubeAIServeRunStatus spec = new KubeAIServeRunStatus();
        spec.configure(data);

        return spec;
    }
}
