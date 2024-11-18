package it.smartcommunitylabdhub.commons.models.workflow;

import it.smartcommunitylabdhub.commons.models.base.ExecutableBaseSpec;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WorkflowBaseSpec extends ExecutableBaseSpec {

    @Override
    public void configure(Map<String, Serializable> data) {}
}
