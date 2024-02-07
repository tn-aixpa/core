package it.smartcommunitylabdhub.modules.mlrun.components.builders;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.builders.Builder;
import it.smartcommunitylabdhub.core.models.entities.run.specs.RunRunSpec;
import it.smartcommunitylabdhub.modules.mlrun.models.specs.function.FunctionMlrunSpec;
import it.smartcommunitylabdhub.modules.mlrun.models.specs.run.RunMlrunSpec;
import it.smartcommunitylabdhub.modules.mlrun.models.specs.task.TaskMlrunSpec;

/**
 * MlrunMlrunBuilder
 * <p>
 * You can use this as a simple class or as a registered bean. If you want to retrieve this as bean from BuilderFactory
 * you have to register it using the following annotation:
 *
 * @BuilderComponent(runtime = "mlrun", task = "mlrun")
 */

public class MlrunMlrunBuilder implements Builder<
        FunctionMlrunSpec,
        TaskMlrunSpec,
        RunRunSpec,
        RunMlrunSpec> {

    @Override
    public RunMlrunSpec build(
            FunctionMlrunSpec funSpec,
            TaskMlrunSpec taskSpec,
            RunRunSpec runSpec) {

        RunMlrunSpec runMlrunSpec = RunMlrunSpec.builder()
                .taskSpec(taskSpec)
                .funcSpec(funSpec)
                .build();

        runMlrunSpec.configure(runSpec.toMap());

        // Return a run spec
        return runMlrunSpec;
    }
}
