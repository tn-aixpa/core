package it.smartcommunitylabdhub.modules.nefertem.components.builders;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.builders.Builder;
import it.smartcommunitylabdhub.core.models.entities.run.specs.RunRunSpec;
import it.smartcommunitylabdhub.modules.nefertem.models.specs.function.FunctionNefertemSpec;
import it.smartcommunitylabdhub.modules.nefertem.models.specs.run.RunNefertemSpec;
import it.smartcommunitylabdhub.modules.nefertem.models.specs.task.TaskMetricSpec;

/**
 * NefetermMetricBuilder
 * <p>
 * You can use this as a simple class or as a registered bean. If you want to retrieve this as bean from BuilderFactory
 * you have to register it using the following annotation:
 *
 * @BuilderComponent(runtime = "nefertem", task = "metric")
 */

public class NefertemMetricBuilder implements Builder<
        FunctionNefertemSpec,
        TaskMetricSpec,
        RunRunSpec,
        RunNefertemSpec<TaskMetricSpec>> {

    @Override
    public RunNefertemSpec<TaskMetricSpec> build(
            FunctionNefertemSpec funSpec,
            TaskMetricSpec taskSpec,
            RunRunSpec runSpec) {

        RunNefertemSpec<TaskMetricSpec> runNefertemSpec =
                RunNefertemSpec.<TaskMetricSpec>builder()
                        .taskNefertemSpec(taskSpec)
                        .functionNefertemSpec(funSpec)
                        .build();

        runNefertemSpec.configure(runSpec.toMap());

        // Return a run spec
        return runNefertemSpec;
    }
}
