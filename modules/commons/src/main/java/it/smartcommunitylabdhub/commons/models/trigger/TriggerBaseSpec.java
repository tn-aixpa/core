package it.smartcommunitylabdhub.commons.models.trigger;

import it.smartcommunitylabdhub.commons.models.base.BaseSpec;
import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TriggerBaseSpec extends BaseSpec {

    @NotEmpty
    private String task;

    private String function;

    private String workflow;

    @NotEmpty
    private Map<String, Serializable> template;

    @Override
    public void configure(Map<String, Serializable> data) {
        TriggerBaseSpec spec = mapper.convertValue(data, TriggerBaseSpec.class);

        this.task = spec.getTask();
        this.function = spec.getFunction();
        this.workflow = spec.getWorkflow();

        this.template = spec.getTemplate();
    }

    public static TriggerBaseSpec from(Map<String, Serializable> data) {
        TriggerBaseSpec spec = new TriggerBaseSpec();
        spec.configure(data);

        return spec;
    }
}
