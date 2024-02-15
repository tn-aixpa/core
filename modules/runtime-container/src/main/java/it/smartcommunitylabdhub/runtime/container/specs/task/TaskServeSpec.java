package it.smartcommunitylabdhub.runtime.container.specs.task;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.runtime.container.ContainerRuntime;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SpecType(runtime = ContainerRuntime.RUNTIME, kind = "container+serve", entity = EntityName.TASK)
public class TaskServeSpec extends TaskDeploySpec {

    /// TODO: Service parameters port list...ClusterIP or NodePort

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);
    }
}
