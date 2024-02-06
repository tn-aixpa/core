package it.smartcommunitylabdhub.modules.nefertem.components.builders;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.builders.Builder;
import it.smartcommunitylabdhub.core.models.entities.run.specs.RunRunSpec;
import it.smartcommunitylabdhub.modules.nefertem.models.specs.function.FunctionNefertemSpec;
import it.smartcommunitylabdhub.modules.nefertem.models.specs.run.RunNefertemSpec;
import it.smartcommunitylabdhub.modules.nefertem.models.specs.task.TaskValidateSpec;

/**
 * NefetermValidateBuilder
 * <p>
 * You can use this as a simple class or as a registered bean. If you want to retrieve this as bean from BuilderFactory
 * you have to register it using the following annotation:
 *
 * @BuilderComponent(runtime = "nefertem", task = "validate")
 */

public class NefertemValidateBuilder implements Builder<
        FunctionNefertemSpec,
        TaskValidateSpec,
        RunRunSpec,
        RunNefertemSpec<TaskValidateSpec>> {

    @Override
    public RunNefertemSpec<TaskValidateSpec> build(
            FunctionNefertemSpec funSpec,
            TaskValidateSpec taskSpec,
            RunRunSpec runSpec) {

        RunNefertemSpec<TaskValidateSpec> runNefertemSpec =
                RunNefertemSpec.<TaskValidateSpec>builder()
                        .k8sTaskBaseSpec(taskSpec)
                        .functionNefertemSpec(funSpec)
                        .build();

        runNefertemSpec.configure(runSpec.toMap());

        // Return a run spec
        return runNefertemSpec;
    }
}
