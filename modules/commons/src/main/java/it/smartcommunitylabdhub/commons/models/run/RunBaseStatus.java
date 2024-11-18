package it.smartcommunitylabdhub.commons.models.run;

import com.fasterxml.jackson.annotation.JsonInclude;
import it.smartcommunitylabdhub.commons.models.base.BaseSpec;
import java.io.Serializable;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder(builderMethodName = "baseBuilder")
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RunBaseStatus extends BaseSpec {

    private String state;
    private String message;

    public RunBaseStatus(String state) {
        this.state = state;
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        RunBaseStatus spec = mapper.convertValue(data, RunBaseStatus.class);

        this.state = spec.getState();
        this.message = spec.getMessage();
    }

    public static RunBaseStatus with(Map<String, Serializable> data) {
        RunBaseStatus spec = new RunBaseStatus();
        spec.configure(data);

        return spec;
    }
}
