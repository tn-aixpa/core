package it.smartcommunitylabdhub.runtime.python.specs;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.framework.k8s.base.K8sFunctionTaskBaseSpec;
import it.smartcommunitylabdhub.runtime.python.PythonRuntime;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SpecType(runtime = PythonRuntime.RUNTIME, kind = PythonBuildTaskSpec.KIND, entity = EntityName.TASK)
public class PythonBuildTaskSpec extends K8sFunctionTaskBaseSpec {

    public static final String KIND = "python+build";

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
