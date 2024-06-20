package it.smartcommunitylabdhub.runtime.nefertem.builders;

import it.smartcommunitylabdhub.commons.infrastructure.Builder;
import it.smartcommunitylabdhub.runtime.nefertem.specs.function.FunctionNefertemSpec;
import it.smartcommunitylabdhub.runtime.nefertem.specs.run.RunNefertemSpec;
import it.smartcommunitylabdhub.runtime.nefertem.specs.task.TaskInferSpec;
import java.util.Optional;

/**
 * NefetermInferBuilder
 * <p>
 * You can use this as a simple class or as a registered bean. If you want to retrieve this as bean from BuilderFactory
 * you have to register it using the following annotation:
 *
 * @BuilderComponent(runtime = "nefertem", task = "infer")
 */

public class NefertemInferBuilder implements Builder<FunctionNefertemSpec, TaskInferSpec, RunNefertemSpec> {

    @Override
    public RunNefertemSpec build(FunctionNefertemSpec funSpec, TaskInferSpec taskSpec, RunNefertemSpec runSpec) {
        RunNefertemSpec runNefertemSpec = new RunNefertemSpec(runSpec.toMap());
        runNefertemSpec.setFuncSpec(funSpec);
        runNefertemSpec.setTaskInferSpec(taskSpec);

        //let run override k8s specs
        Optional
            .ofNullable(runSpec.getTaskInferSpec())
            .ifPresent(k8sSpec -> runNefertemSpec.getTaskInferSpec().configure(k8sSpec.toMap()));

        // Return a run spec
        return runNefertemSpec;
    }
}
