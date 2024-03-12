package it.smartcommunitylabdhub.runtime.nefertem.status;

import com.fasterxml.jackson.annotation.JsonInclude;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseStatus;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RunNefertemStatus extends RunBaseStatus {

    @Override
    public void configure(Map<String, Serializable> data) {
        RunNefertemStatus meta = mapper.convertValue(data, RunNefertemStatus.class);
    }
}
