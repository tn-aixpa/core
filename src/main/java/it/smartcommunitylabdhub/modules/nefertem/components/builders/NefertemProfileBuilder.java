package it.smartcommunitylabdhub.modules.nefertem.components.builders;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.builders.Builder;
import it.smartcommunitylabdhub.core.models.entities.run.specs.RunRunSpec;
import it.smartcommunitylabdhub.modules.nefertem.models.specs.function.FunctionNefertemSpec;
import it.smartcommunitylabdhub.modules.nefertem.models.specs.run.RunNefertemSpec;
import it.smartcommunitylabdhub.modules.nefertem.models.specs.task.TaskProfileSpec;

/**
 * NefetermProfileBuilder
 * <p>
 * You can use this as a simple class or as a registered bean. If you want to retrieve this as bean from BuilderFactory
 * you have to register it using the following annotation:
 *
 * @BuilderComponent(runtime = "nefertem", task = "profile")
 */

public class NefertemProfileBuilder implements Builder<
        FunctionNefertemSpec,
        TaskProfileSpec,
        RunRunSpec,
        RunNefertemSpec<TaskProfileSpec>> {

    @Override
    public RunNefertemSpec<TaskProfileSpec> build(
            FunctionNefertemSpec funSpec,
            TaskProfileSpec taskSpec,
            RunRunSpec runSpec) {

        RunNefertemSpec<TaskProfileSpec> runNefertemSpec =
                RunNefertemSpec.<TaskProfileSpec>builder()
                        .k8sTaskBaseSpec(taskSpec)
                        .functionNefertemSpec(funSpec)
                        .build();

        runNefertemSpec.configure(runSpec.toMap());

        // Return a run spec
        return runNefertemSpec;
    }
}
