package it.smartcommunitylabdhub.runtime.python.specs.task;

import io.swagger.v3.oas.annotations.media.Schema;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.framework.k8s.base.K8sTaskBaseSpec;
import it.smartcommunitylabdhub.runtime.python.PythonRuntime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@SpecType(runtime = PythonRuntime.RUNTIME, kind = PythonBuildTaskSpec.KIND, entity = EntityName.TASK)
public class PythonBuildTaskSpec extends K8sTaskBaseSpec {

    public static final String KIND = "python+build";

    @Schema(title = "fields.container.instructions.title", description = "fields.container.instructions.description")
    private List<String> instructions;

    public PythonBuildTaskSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        PythonBuildTaskSpec spec = mapper.convertValue(data, PythonBuildTaskSpec.class);
        this.instructions = spec.getInstructions();
    }
}
