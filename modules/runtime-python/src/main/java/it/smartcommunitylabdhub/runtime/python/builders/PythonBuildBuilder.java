package it.smartcommunitylabdhub.runtime.python.builders;

import it.smartcommunitylabdhub.commons.infrastructure.Builder;
import it.smartcommunitylabdhub.runtime.python.specs.function.PythonFunctionSpec;
import it.smartcommunitylabdhub.runtime.python.specs.run.PythonRunSpec;
import it.smartcommunitylabdhub.runtime.python.specs.task.PythonBuildTaskSpec;
import it.smartcommunitylabdhub.runtime.python.specs.task.PythonJobTaskSpec;

import java.util.Optional;

public class PythonBuildBuilder implements Builder<PythonFunctionSpec, PythonBuildTaskSpec, PythonRunSpec> {

    @Override
    public PythonRunSpec build(PythonFunctionSpec funSpec, PythonBuildTaskSpec taskSpec, PythonRunSpec runSpec) {
        PythonRunSpec pythonSpec = new PythonRunSpec(runSpec.toMap());
        pythonSpec.setTaskBuildSpec(taskSpec);
        pythonSpec.setFunctionSpec(funSpec);

        //let run override k8s specs
        Optional
                .ofNullable(runSpec.getTaskJobSpec())
                .ifPresent(k8sSpec -> pythonSpec.getTaskJobSpec().configure(k8sSpec.toMap()));

        return pythonSpec;
    }
}
