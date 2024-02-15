package it.smartcommunitylabdhub.runtime.mlrun.builders;

import it.smartcommunitylabdhub.commons.infrastructure.Builder;
import it.smartcommunitylabdhub.runtime.mlrun.specs.function.FunctionMlrunSpec;
import it.smartcommunitylabdhub.runtime.mlrun.specs.run.RunMlrunSpec;
import it.smartcommunitylabdhub.runtime.mlrun.specs.task.TaskMlrunSpec;

/**
 * MlrunMlrunBuilder
 * <p>
 * You can use this as a simple class or as a registered bean. If you want to retrieve this as bean from BuilderFactory
 * you have to register it using the following annotation:
 *
 * @BuilderComponent(runtime = "mlrun", task = "mlrun")
 */

public class MlrunMlrunBuilder implements Builder<FunctionMlrunSpec, TaskMlrunSpec, RunMlrunSpec> {

    @Override
    public RunMlrunSpec build(FunctionMlrunSpec funSpec, TaskMlrunSpec taskSpec, RunMlrunSpec runSpec) {
        RunMlrunSpec runMlrunSpec = new RunMlrunSpec(runSpec.toMap());
        runMlrunSpec.setTaskSpec(taskSpec);
        runMlrunSpec.setFuncSpec(funSpec);

        // Return a run spec
        return runMlrunSpec;
    }
}
