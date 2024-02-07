package it.smartcommunitylabdhub.modules.nefertem.components.builders;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.builders.Builder;
import it.smartcommunitylabdhub.core.models.entities.run.specs.RunRunSpec;
import it.smartcommunitylabdhub.modules.nefertem.models.specs.function.FunctionNefertemSpec;
import it.smartcommunitylabdhub.modules.nefertem.models.specs.run.RunNefertemSpec;
import it.smartcommunitylabdhub.modules.nefertem.models.specs.task.TaskInferSpec;

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
        RunRunSpec,
        RunNefertemSpec<TaskInferSpec>> {

    @Override
    public RunNefertemSpec<TaskInferSpec> build(
            FunctionNefertemSpec funSpec,
            TaskInferSpec taskSpec,
            RunRunSpec runSpec) {

        RunNefertemSpec<TaskInferSpec> runNefertemSpec =
                RunNefertemSpec.<TaskInferSpec>builder()
                        .build();

        runNefertemSpec.configure(runSpec.toMap());
        runNefertemSpec.setFuncSpec(funSpec);
        runNefertemSpec.setTaskSpec(taskSpec);

        // Return a run spec
        return runNefertemSpec;
    }
}
