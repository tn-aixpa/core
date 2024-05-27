package it.smartcommunitylabdhub.runtime.python.builders;


import it.smartcommunitylabdhub.commons.infrastructure.Builder;
import it.smartcommunitylabdhub.runtime.python.specs.function.FunctionPythonSpec;
import it.smartcommunitylabdhub.runtime.python.specs.run.RunPythonSpec;
import it.smartcommunitylabdhub.runtime.python.specs.task.TaskBuildSpec;

import java.util.Optional;

/**
 * PythonJobBuilder
 * <p>
 * You can use this as a simple class or as a registered bean. If you want to retrieve this as bean from BuilderFactory
 * you have to register it using the following annotation:
 *
 * @BuilderComponent(runtime = "python", task = "build")
 */

public class PythonBuildBuilder implements Builder<FunctionPythonSpec, TaskBuildSpec, RunPythonSpec> {

    @Override
    public RunPythonSpec build(FunctionPythonSpec funSpec, TaskBuildSpec taskSpec, RunPythonSpec runSpec) {
        RunPythonSpec pythonSpec = new RunPythonSpec(runSpec.toMap());
        pythonSpec.setTaskBuildSpec(taskSpec);
        pythonSpec.setFunctionSpec(funSpec);

        //let run override k8s specs
        Optional
            .ofNullable(runSpec.getTaskJobSpec())
            .ifPresent(k8sSpec -> pythonSpec.getTaskJobSpec().configure(k8sSpec.toMap()));

        return pythonSpec;
    }
}
