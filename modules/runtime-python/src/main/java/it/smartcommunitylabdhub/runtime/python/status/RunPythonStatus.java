package it.smartcommunitylabdhub.runtime.python.status;

import com.fasterxml.jackson.annotation.JsonInclude;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseStatus;
import lombok.*;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RunPythonStatus extends RunBaseStatus {

    private Map<String, Serializable> k8s;

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        RunPythonStatus spec = mapper.convertValue(data, RunPythonStatus.class);
        this.k8s = spec.getK8s();
    }
}
