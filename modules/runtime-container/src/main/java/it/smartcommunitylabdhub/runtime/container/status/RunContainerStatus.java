package it.smartcommunitylabdhub.runtime.container.status;

import com.fasterxml.jackson.annotation.JsonInclude;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseStatus;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RunContainerStatus extends RunBaseStatus {

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);
        RunContainerStatus meta = mapper.convertValue(data, RunContainerStatus.class);
    }
}
