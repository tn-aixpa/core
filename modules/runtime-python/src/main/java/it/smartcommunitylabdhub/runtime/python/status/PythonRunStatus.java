package it.smartcommunitylabdhub.runtime.python.status;

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
public class PythonRunStatus extends RunBaseStatus {

    private Map<String, Serializable> k8s;

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        PythonRunStatus spec = mapper.convertValue(data, PythonRunStatus.class);
        this.k8s = spec.getK8s();
    }
}
