package it.smartcommunitylabdhub.commons.models.log;

import it.smartcommunitylabdhub.commons.models.base.BaseSpec;
import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LogBaseSpec extends BaseSpec {

    @NotEmpty
    private String run;

    private Long timestamp;

    @Override
    public void configure(Map<String, Serializable> data) {
        LogBaseSpec spec = mapper.convertValue(data, LogBaseSpec.class);

        this.run = spec.getRun();
        this.timestamp = spec.getTimestamp();
    }
}
