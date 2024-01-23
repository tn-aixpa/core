package it.smartcommunitylabdhub.core.models.entities.workflow.specs;

import it.smartcommunitylabdhub.core.models.base.specs.BaseSpec;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class WorkflowBaseSpec extends BaseSpec {
    @Override
    public void configure(Map<String, Object> data) {
        super.configure(data);
    }
}
