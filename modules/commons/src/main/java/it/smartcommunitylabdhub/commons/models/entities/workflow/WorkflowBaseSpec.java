package it.smartcommunitylabdhub.commons.models.entities.workflow;

import it.smartcommunitylabdhub.commons.models.base.BaseSpec;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WorkflowBaseSpec extends BaseSpec {

    @Override
    public void configure(Map<String, Serializable> data) {}
}
