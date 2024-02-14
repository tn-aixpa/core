package it.smartcommunitylabdhub.commons.models.entities.workflow;

import it.smartcommunitylabdhub.commons.models.base.BaseSpec;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkflowBaseSpec extends BaseSpec {

    @Override
    public void configure(Map<String, Object> data) {
        super.configure(data);
    }
}
