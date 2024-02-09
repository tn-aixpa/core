package it.smartcommunitylabdhub.runtime.nefertem.builders;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.builders.Builder;
import it.smartcommunitylabdhub.runtime.nefertem.models.specs.function.FunctionNefertemSpec;
import it.smartcommunitylabdhub.runtime.nefertem.models.specs.run.RunNefertemSpec;
import it.smartcommunitylabdhub.runtime.nefertem.models.specs.task.TaskInferSpec;

/**
 * NefetermInferBuilder
 * <p>
 * You can use this as a simple class or as a registered bean. If you want to retrieve this as bean from BuilderFactory
 * you have to register it using the following annotation:
 *
 * @BuilderComponent(runtime = "nefertem", task = "infer")
 */

public class NefertemInferBuilder implements Builder<
        FunctionNefertemSpec,
        TaskInferSpec,
        RunNefertemSpec> {

    @Override
    public RunNefertemSpec build(
            FunctionNefertemSpec funSpec,
            TaskInferSpec taskSpec,
            RunNefertemSpec runSpec) {

        RunNefertemSpec runNefertemSpec =
                RunNefertemSpec.builder()
                        .build();

        runNefertemSpec.configure(runSpec.toMap());
        runNefertemSpec.setFuncSpec(funSpec);
        runNefertemSpec.setTaskInferSpec(taskSpec);

        // Return a run spec
        return runNefertemSpec;
    }
}
