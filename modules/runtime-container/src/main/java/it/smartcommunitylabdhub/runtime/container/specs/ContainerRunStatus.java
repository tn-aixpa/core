package it.smartcommunitylabdhub.runtime.container.specs;

import com.fasterxml.jackson.annotation.JsonInclude;
import it.smartcommunitylabdhub.commons.models.run.RunBaseStatus;
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
public class ContainerRunStatus extends RunBaseStatus {

    private Map<String, Serializable> k8s;

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        ContainerRunStatus spec = mapper.convertValue(data, ContainerRunStatus.class);
        this.k8s = spec.getK8s();
    }
}
