package it.smartcommunitylabdhub.runtime.kfp.specs.workflow;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.runtime.kfp.KFPRuntime;
import it.smartcommunitylabdhub.runtime.kfp.specs.function.FunctionKFPSpec;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SpecType(runtime = KFPRuntime.RUNTIME, kind = KFPRuntime.RUNTIME + "-workflow", entity = EntityName.WORKFLOW)
public class WorkflowKFPSpec extends FunctionKFPSpec {

    public WorkflowKFPSpec(Map<String, Serializable> data) {
        configure(data);
    }
}
