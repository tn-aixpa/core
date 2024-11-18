package it.smartcommunitylabdhub.runtime.container.specs;

import io.swagger.v3.oas.annotations.media.Schema;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.framework.k8s.base.K8sFunctionTaskBaseSpec;
import it.smartcommunitylabdhub.runtime.container.ContainerRuntime;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SpecType(runtime = ContainerRuntime.RUNTIME, kind = ContainerBuildTaskSpec.KIND, entity = EntityName.TASK)
public class ContainerBuildTaskSpec extends K8sFunctionTaskBaseSpec {

    public static final String KIND = "container+build";

    @Schema(title = "fields.container.instructions.title", description = "fields.container.instructions.description")
    private List<String> instructions;

    public ContainerBuildTaskSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        ContainerBuildTaskSpec spec = mapper.convertValue(data, ContainerBuildTaskSpec.class);
        this.instructions = spec.getInstructions();
    }
}
