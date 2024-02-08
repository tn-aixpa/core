package it.smartcommunitylabdhub.modules.dbt.components.builders;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.builders.Builder;
import it.smartcommunitylabdhub.modules.dbt.models.specs.function.FunctionDbtSpec;
import it.smartcommunitylabdhub.modules.dbt.models.specs.run.RunDbtSpec;
import it.smartcommunitylabdhub.modules.dbt.models.specs.task.TaskTransformSpec;

/**
 * DbtTransformBuilder
 * <p>
 * You can use this as a simple class or as a registered bean. If you want to retrieve this as bean from BuilderFactory
 * you have to register it using the following annotation:
 *
 * @BuilderComponent(runtime = "dbt", task = "transform")
 */

public class DbtTransformBuilder implements Builder<
        FunctionDbtSpec,
        TaskTransformSpec,
        RunDbtSpec> {
    @Override
    public RunDbtSpec build(
            FunctionDbtSpec funSpec,
            TaskTransformSpec taskSpec,
            RunDbtSpec runSpec) {

        RunDbtSpec runDbtSpec = RunDbtSpec
                .builder()
                .build();

        runDbtSpec.configure(runSpec.toMap());
        runDbtSpec.setTaskSpec(taskSpec);
        runDbtSpec.setFuncSpec(funSpec);

        return runDbtSpec;
    }
}
