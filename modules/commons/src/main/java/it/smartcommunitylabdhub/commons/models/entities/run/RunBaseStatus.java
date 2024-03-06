package it.smartcommunitylabdhub.commons.models.entities.run;

import com.fasterxml.jackson.annotation.JsonInclude;
import it.smartcommunitylabdhub.commons.models.base.BaseSpec;
import java.io.Serializable;
import java.util.Map;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder(builderMethodName = "baseBuilder")
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RunBaseStatus extends BaseSpec {
    private String status;

    @Override
    public void configure(Map<String, Serializable> data) {
        RunBaseStatus meta = mapper.convertValue(data, RunBaseStatus.class);

        this.status = meta.getStatus();
    }
}
