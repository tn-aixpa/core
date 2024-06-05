package it.smartcommunitylabdhub.runtime.python.builders;

import it.smartcommunitylabdhub.commons.infrastructure.Builder;
import it.smartcommunitylabdhub.runtime.python.specs.function.PythonFunctionSpec;
import it.smartcommunitylabdhub.runtime.python.specs.run.PythonRunSpec;
import it.smartcommunitylabdhub.runtime.python.specs.task.PythonServeTaskSpec;
import java.util.Optional;

public class PythonServeBuilder implements Builder<PythonFunctionSpec, PythonServeTaskSpec, PythonRunSpec> {

    @Override
    public PythonRunSpec build(PythonFunctionSpec funSpec, PythonServeTaskSpec taskSpec, PythonRunSpec runSpec) {
        PythonRunSpec pythonSpec = new PythonRunSpec(runSpec.toMap());
        pythonSpec.setTaskServeSpec(taskSpec);
        pythonSpec.setFunctionSpec(funSpec);

        //let run override k8s specs
        Optional
            .ofNullable(runSpec.getTaskServeSpec())
            .ifPresent(k8sSpec -> pythonSpec.getTaskServeSpec().configure(k8sSpec.toMap()));

        return pythonSpec;
    }
}
