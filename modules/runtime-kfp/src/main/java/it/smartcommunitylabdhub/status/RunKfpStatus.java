package it.smartcommunitylabdhub.status;

import com.fasterxml.jackson.annotation.JsonInclude;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseStatus;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RunKfpStatus extends RunBaseStatus {

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);
    }
}
